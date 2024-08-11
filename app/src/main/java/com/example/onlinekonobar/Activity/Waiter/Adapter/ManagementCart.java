package com.example.onlinekonobar.Activity.Waiter.Adapter;

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
import com.example.onlinekonobar.Api.Normative;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.TinyDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        checkStockAndUpdateQuantity(articleId, false, new com.example.onlinekonobar.Activity.User.Adapter.ManagementCart.StockUpdateCallback() {
            @Override
            public void onStockUpdate(boolean success) {}
        });
    }

    public void checkStockAndUpdateQuantity(int articleId, boolean increment, com.example.onlinekonobar.Activity.User.Adapter.ManagementCart.StockUpdateCallback callback) {
        userService.getNormativeByArticleId(articleId).enqueue(new Callback<ArrayList<Normative>>() {
            @Override
            public void onResponse(Call<ArrayList<Normative>> call, Response<ArrayList<Normative>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Normative> normatives = response.body();
                    ArrayList<Item> cartList = getListCart();
                    List<Call<Stock>> stockCalls = new ArrayList<>();

                    for (Normative normative : normatives) {
                        stockCalls.add(userService.getStockById(normative.Skladiste_Id));
                    }
                    if (stockCalls.isEmpty()) {
                        callback.onStockUpdate(false);
                        return;
                    }

                    List<Stock> stockList = new ArrayList<>();
                    final int[] pendingRequests = {stockCalls.size()};

                    for (Call<Stock> stockCall : stockCalls) {
                        stockCall.enqueue(new Callback<Stock>() {
                            @Override
                            public void onResponse(Call<Stock> call, Response<Stock> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    stockList.add(response.body());
                                }

                                if (--pendingRequests[0] == 0) {
                                    boolean canUpdate = true;
                                    for (Item item : cartList) {
                                        if (item.getArtikal_Id() == articleId) {
                                            int newQuantity = increment ? item.getKolicina() + 1 : item.getKolicina() - 1;
                                            for (int i = 0; i < normatives.size(); i++) {
                                                Normative normative = normatives.get(i);
                                                Stock stock = stockList.get(i);

                                                if (increment && newQuantity * normative.Normativ > stock.getKolicina()) {
                                                    canUpdate = false;
                                                    break;
                                                }
                                            }
                                            if (canUpdate) {
                                                item.setKolicina(newQuantity);
                                                tinyDB.putListObject("CartList", cartList);
                                                if (updateTotalFeeCallback != null) {
                                                    updateTotalFeeCallback.run();
                                                }
                                                callback.onStockUpdate(true);
                                            } else {
                                                Toast.makeText(context, "Nedovoljno artikala na skladištu.", Toast.LENGTH_SHORT).show();
                                                callback.onStockUpdate(false);

                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<Stock> call, Throwable t) {
                                if (--pendingRequests[0] == 0) {
                                    callback.onStockUpdate(false);
                                }
                            }
                        });
                    }
                } else {
                    Log.e("NormativeFetch", "Neuspješno dobavljanje normativa: " + response.code() + " - " + response.message());
                    callback.onStockUpdate(false);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Normative>> call, Throwable t) {
                Log.e("NormativeFetch", "Greška u komunikaciji sa serverom prilikom dobavljanja normativa.", t);
                callback.onStockUpdate(false);
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
    public void getTotalFee(final com.example.onlinekonobar.Activity.User.Adapter.ManagementCart.TotalFeeCallback callback) {
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
        SharedPreferences sharedPreferences = context.getSharedPreferences("QRPrefs", Context.MODE_PRIVATE);
        String table = sharedPreferences.getString("qrValue", null);
        ArrayList<Item> cartList = getListCart();
        if (cartList != null && !cartList.isEmpty()) {
            String orderId = generateOrderId();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            String formattedDate = sdf.format(currentTime);
            float totalAmount = 0;
            int dokument_Id = -1;

            for (Item item : cartList) {
                totalAmount += item.getCijena() * item.getKolicina();
                dokument_Id = item.getDokument_Id();
            }

            Invoice invoice = new Invoice();
            invoice.setDokument_Id(dokument_Id);
            invoice.setBroj_Racuna(orderId);
            invoice.setStatus("Aktivno");
            invoice.setUkupan_Iznos(totalAmount);
            invoice.setDatum(formattedDate);
            invoice.setKorisnik_Id(userId);
            invoice.setPreuzeto(true);
            invoice.setStol(table);

            userService.saveInvoice(invoice).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Narudžba je kreirana", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SelectTable.class);
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

            // Prikupiti normative i ažurirati stanje skladišta
            Map<Integer, Integer> totalNormatives = new HashMap<>();

            for (Item item : cartList) {
                item.setKorisnik_Id(userId);
                item.setOrder_Id(orderId);

                userService.getNormativeByArticleId(item.getArtikal_Id()).enqueue(new Callback<ArrayList<Normative>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Normative>> call, Response<ArrayList<Normative>> response) {
                        if (response.isSuccessful()) {
                            List<Normative> existingNormatives = response.body();
                            if (existingNormatives != null) {
                                for (Normative normative : existingNormatives) {
                                    int totalQuantity = totalNormatives.getOrDefault(normative.Skladiste_Id, 0) +
                                            normative.Normativ * item.getKolicina();
                                    totalNormatives.put(normative.Skladiste_Id, totalQuantity);
                                }
                                updateStockQuantities(totalNormatives);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Normative>> call, Throwable t) {
                        Log.e("NormativeFetch", "Greška u komunikaciji sa serverom prilikom dobavljanja normativa.", t);
                        Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void updateStockQuantities(Map<Integer, Integer> totalNormatives) {
        for (Map.Entry<Integer, Integer> entry : totalNormatives.entrySet()) {
            int skladisteId = entry.getKey();
            int totalNormativeQuantity = entry.getValue();

            userService.getStockById(skladisteId).enqueue(new Callback<Stock>() {
                @Override
                public void onResponse(Call<Stock> call, Response<Stock> response) {
                    if (response.isSuccessful()) {
                        Stock existingStock = response.body();
                        if (existingStock != null) {
                            int newStockQuantity = existingStock.getKolicina() - totalNormativeQuantity;
                            if (newStockQuantity >= 0) {
                                existingStock.setId(existingStock.getId());
                                existingStock.setArtikal(existingStock.getArtikal());
                                existingStock.setDokument_Id(existingStock.getDokument_Id());
                                existingStock.setKorisnik_Id(existingStock.getKorisnik_Id());
                                existingStock.setKolicina(newStockQuantity);


                                userService.updateStock(existingStock.getId(), existingStock).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Stanje skladišta uspješno ažurirano.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Greška u ažuriranju stanja skladišta.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("StockUpdate", "Greška u komunikaciji sa serverom prilikom ažuriranja stanja skladišta.", t);
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Nedovoljno stavki na skladištu.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Stock> call, Throwable t) {
                    Log.e("StockFetch", "Greška u komunikaciji sa serverom prilikom dobavljanja stanja skladišta.", t);
                    Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}