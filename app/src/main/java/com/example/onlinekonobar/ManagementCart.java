package com.example.onlinekonobar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.onlinekonobar.Activity.User.Articles;
import com.example.onlinekonobar.Activity.User.Card;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.TinyDB;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;
    private UserService userService;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
        this.userService = Client.getService();
    }

    public ArrayList<Item> getListCart() {
        return tinyDB.getListObject("CartList", Item.class);
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
    }

    public void deleteArticle(int articleId) {
        ArrayList<Item> cartList = getListCart();
        if (cartList != null) {
            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.get(i).getArtikal_Id() == articleId) {
                    cartList.remove(i);
                    tinyDB.putListObject("CartList", cartList);
                    break;
                }
            }
        }
    }

    public void incrementQuantity(int articleId) {
        ArrayList<Item> cartList = getListCart();
        for (Item item : cartList) {
            if (item.getArtikal_Id() == articleId) {
                item.setKolicina(item.getKolicina() + 1);
                Log.d("Test", "Test" + item.getKolicina());
                // Ažuriraj promjene u trajnoj pohrani
                tinyDB.putListObject("CartList", cartList);
                return;
            }
        }
    }

    public void decrementQuantity(int articleId) {
        ArrayList<Item> cartList = getListCart();
        for (Item item : cartList) {
            if (item.getArtikal_Id() == articleId) {
                if (item.getKolicina() != 1) {
                    item.setKolicina(item.getKolicina() - 1);
                }
                Log.d("Test", "Test" + item.getKolicina());
                // Ažuriraj promjene u trajnoj pohrani
                tinyDB.putListObject("CartList", cartList);
                return;
            }
        }
    }


    public int getItemQuantity(int articleId, int customizeId) {
        ArrayList<Item> cartList = getListCart();
        for (Item item : cartList) {
            if (item.getArtikal_Id() == articleId && item.getDodatak() == customizeId) {
                return item.getKolicina();
            }
        }
        return 1; // Ili bilo koja druga zadana vrijednost
    }

    public void clearCart() {
        ArrayList<Item> emptyList = new ArrayList<>();
        tinyDB.putListObject("CartList", emptyList);
        Toast.makeText(context, "Košarica je očišćena", Toast.LENGTH_SHORT).show();
    }
    public interface TotalFeeCallback {
        void onTotalFeeCalculated(double totalFee);
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
                    Log.e("CartInfo", "Error fetching article with ID: " + item.getArtikal_Id(), t);
                    if (--pendingRequests[0] == 0) {
                        callback.onTotalFeeCalculated(total[0]);
                    }
                }
            });
        }
    }


}
