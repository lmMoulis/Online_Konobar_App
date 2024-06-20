package com.example.onlinekonobar.Activity.User.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.onlinekonobar.Activity.User.ScanQR;
import com.example.onlinekonobar.Activity.Waiter.SelectTable;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.TinyDB;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

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
            newItem.setCijena(article.getCijena());
            cartList.add(newItem);
            Log.d("ManagementCart", "Added new item to cart: " + newItem.getArtikal_Id() + ", " + newItem.getDodatak() + ", " + newItem.getCijena() + ", " + newItem.getKorisnik_Id());
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
//        Toast.makeText(context, "Košarica je očišćena", Toast.LENGTH_SHORT).show();
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

    private String generateOrderId() {
        String orderId =  UUID.randomUUID().toString();
        return orderId;

    }
    public void saveCartToDatabase(int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("QRPrefs",Context.MODE_PRIVATE);
        String table=sharedPreferences.getString("qrValue",null);
        ArrayList<Item> cartList = getListCart();
        if (cartList != null && !cartList.isEmpty()) {

            String orderId = generateOrderId();

            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            String formattedDate = sdf.format(currentTime);
            float totalAmount = 0;
            int dokument_Id=-1;

            for(Item item:cartList){
                totalAmount+=item.getCijena()*item.getKolicina();
                dokument_Id=item.getDokument_Id();
            }
            Invoice invoice= new Invoice();
            invoice.setDokument_Id(dokument_Id);
            invoice.setBroj_Racuna(orderId);
            invoice.setStatus("Aktivno");
            invoice.setUkupan_Iznos(totalAmount);
            invoice.setDatum(formattedDate);
            invoice.setKorisnik_Id(userId);
            invoice.setPreuzeto(false);
            invoice.setStol(table);

            userService.saveInvoice(invoice).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Narudžba je kreirana", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, ScanQR.class);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Greška prilikom kreiranja narudžbe.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                }
            });

            for (Item item : cartList) {
                item.setKorisnik_Id(userId);
                item.setOrder_Id(orderId);
                totalAmount += item.getCijena() * item.getKolicina();

                Log.d("ManagementCart", "Spremam u bazu: " +
                        "Artikal ID: " + item.getArtikal_Id() +
                        ", Korisnik ID: " + item.getKorisnik_Id() +
                        ", Dokument ID: " + item.getDokument_Id() +
                        ", Količina: " + item.getKolicina() +
                        ", Dodatak ID: " + item.getDodatak()+
                        ". Cijena:"+ item.getCijena());

                userService.saveCard(item).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
//                            Toast.makeText(context, "Podaci iz košarice su spremljeni.", Toast.LENGTH_SHORT).show();
                            clearCart();

                        } else {
//                            Toast.makeText(context, "Greška prilikom spremanja podataka.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                    }
                });

                // Prvo dohvatite trenutno stanje skladišta za dati artikal_id
                userService.getStockByArticleId(item.getArtikal_Id()).enqueue(new Callback<Stock>() {
                    @Override
                    public void onResponse(Call<Stock> call, Response<Stock> response) {
                        if (response.isSuccessful()) {
                            Stock existingStock = response.body();
                            if (existingStock != null) {
                                existingStock.setId(existingStock.getId());
                                existingStock.setArtikal_Id(item.getArtikal_Id());
                                existingStock.setKolicina(existingStock.getKolicina() - item.getKolicina());
                                userService.updateStock(existingStock.getId(), existingStock).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<Stock> call, Throwable t) {
                        Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else {

        }
    }


}
