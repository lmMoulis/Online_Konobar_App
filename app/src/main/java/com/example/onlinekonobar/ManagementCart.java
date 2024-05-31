package com.example.onlinekonobar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.TinyDB;
import com.example.onlinekonobar.ManagementCart.StockUpdateCallback;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;
    private UserService userService;
    private Runnable updateTotalFeeCallback;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
        this.userService = Client.getService();
    }

    public interface StockUpdateCallback {
        void onStockUpdate(boolean success);
    }
    public interface TotalFeeCallback {
        void onTotalFeeCalculated(double totalFee);
    }

    public ArrayList<Item> getListCart() {
        return tinyDB.getListObject("CartList", Item.class);
    }

    public void setUpdateTotalFeeCallback(Runnable updateTotalFeeCallback) {
        this.updateTotalFeeCallback = updateTotalFeeCallback;
    }

    public void insertArticle(Article article, Customize customize, int quantity, int userId, int documentId) {
        ArrayList<Item> cartList = getListCart();
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        boolean existAlready = false;
        int index = 0;
        for (int i = 0; i < cartList.size(); i++) {
            Item item = cartList.get(i);
            if (item.getArtikal_Id() == article.getId() && item.getDodatak() == customize.getId()) {
                existAlready = true;
                index = i;
                break;
            }
        }

        if (existAlready) {
            cartList.get(index).setKolicina(cartList.get(index).getKolicina() + quantity);
        } else {
            Item newItem = new Item();
            newItem.setArtikal_Id(article.getId());
            newItem.setKorisnik_Id(userId);
            newItem.setDokument_Id(documentId);
            newItem.setKolicina(quantity);
            newItem.setDodatak(customize.getId());
            cartList.add(newItem);
            Log.d("ManagementCart", "Added new item to cart: " + newItem.getArtikal_Id() + ", " + newItem.getDodatak() + ", " + newItem.getKolicina());
        }

        tinyDB.putListObject("CartList", cartList);
        Toast.makeText(context, "Artikal je dodan u košaricu", Toast.LENGTH_SHORT).show();
        if (updateTotalFeeCallback != null) {
            updateTotalFeeCallback.run(); // Invoke the callback to update total fee
        }
    }

    public void deleteArticle(int articleId) {
        ArrayList<Item> cartList = getListCart();
        if (cartList != null) {
            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.get(i).getArtikal_Id() == articleId) {
                    cartList.remove(i);
                    tinyDB.putListObject("CartList", cartList);
                    if (updateTotalFeeCallback != null) {
                        updateTotalFeeCallback.run(); // Invoke the callback to update total fee
                    }
                    break;
                }
            }
        }
    }
    public void decrementQuantity(int articleId) {
        checkStockAndUpdateQuantity(articleId, false, new StockUpdateCallback() {
            @Override
            public void onStockUpdate(boolean success) {}
        });
    }

    public void checkStockAndUpdateQuantity(int articleId, boolean increment, StockUpdateCallback callback) {
        userService.getStockByArticleId(articleId).enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Stock stock = response.body();
                    ArrayList<Item> cartList = getListCart();

                    for (Item item : cartList) {
                        if (item.getArtikal_Id() == articleId) {
                            int newQuantity = increment ? item.getKolicina() + 1 : item.getKolicina() - 1;

                            if (increment && newQuantity <= stock.getKolicina()) {
                                item.setKolicina(newQuantity);
                            } else if (!increment && item.getKolicina() > 1) {
                                item.setKolicina(newQuantity);
                            } else {
                                Toast.makeText(context, "Nedovoljno artikala na skladištu.", Toast.LENGTH_SHORT).show();
                                callback.onStockUpdate(false);
                                return;
                            }
                            tinyDB.putListObject("CartList", cartList);
                            if (updateTotalFeeCallback != null) {
                                updateTotalFeeCallback.run(); // Pozivamo callback za ažuriranje ukupne cijene
                            }
                            callback.onStockUpdate(true); // Pozivamo callback s true ako je ažuriranje uspjelo
                            return;
                        }
                    }
                } else {
                    Toast.makeText(context, "Greška prilikom provjere skladišta.", Toast.LENGTH_SHORT).show();
                    callback.onStockUpdate(false); // Pozivamo callback s false ako ažuriranje nije uspjelo
                }
            }

            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                callback.onStockUpdate(false); // Pozivamo callback s false ako ažuriranje nije uspjelo
            }
        });
    }



    public int getItemQuantity(int articleId, int customizeId) {
        ArrayList<Item> cartList = getListCart();
        for (Item item : cartList) {
            if (item.getArtikal_Id() == articleId && item.getDodatak() == customizeId) {
                return item.getKolicina();
            }
        }
        return 1;
    }

    public void clearCart() {
        ArrayList<Item> emptyList = new ArrayList<>();
        tinyDB.putListObject("CartList", emptyList);
        Toast.makeText(context, "Košarica je očišćena", Toast.LENGTH_SHORT).show();
        if (updateTotalFeeCallback != null) {
            updateTotalFeeCallback.run();
        }
    }
    public void getTotalFee(final TotalFeeCallback callback) {
        ArrayList<Item> cartList = getListCart();
        double[] total = {0};
        final int[] pendingRequests = {cartList.size()};

        for (final Item item : cartList) {
            userService.getArticleById(item.getArtikal_Id()).enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Article article = response.body();
                        total[0] += article.getCijena() * item.getKolicina();
                    }
                    if (--pendingRequests[0] == 0) {
                        callback.onTotalFeeCalculated(total[0]);
                    }
                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    if (--pendingRequests[0] == 0) {
                        callback.onTotalFeeCalculated(total[0]);
                    }
                }
            });
        }
    }
}
