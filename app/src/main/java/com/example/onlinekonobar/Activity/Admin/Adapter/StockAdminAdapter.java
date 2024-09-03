package com.example.onlinekonobar.Activity.Admin.Adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.onlinekonobar.Activity.User.Adapter.ManagementCart;
import com.example.onlinekonobar.Api.Adjustment;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Receipt;
import com.example.onlinekonobar.Api.Remaining;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockAdminAdapter extends RecyclerView.Adapter<StockAdminAdapter.viewholder> {

    private ArrayList<Stock> itemsStock;
    private ArrayList<Remaining>itemsRemaining;
    private Context context;
    private int idUser;


    public StockAdminAdapter(ArrayList<Stock> itemsStock, ArrayList<Remaining>itemsRemaining,Context context, int idUser) {
        this.itemsStock = itemsStock;
        this.itemsRemaining=itemsRemaining;
        this.context = context;
        this.idUser = idUser;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_stock_admin, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        Stock stock = itemsStock.get(position);
        Remaining remaining = itemsRemaining.get(position);
        holder.title.setText(stock.getArtikal());
        holder.number.setText(String.valueOf(stock.getKolicina()));
        holder.day.setText(remaining.getDays_Remaining() + " dana");

        Glide.with(context)
                .load(itemsStock.get(position).getSlike())
                .transform(new FitCenter(), new RoundedCorners(20))
                .into(holder.img);

        holder.selectedValue = 1;

        // Postavljanje OnClickListenera za plus i minus
        holder.plus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.number.getText().toString());
            updateQuantity(stock.getId(), currentQuantity + holder.selectedValue, holder, true);
        });

        holder.minus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.number.getText().toString());
            if (currentQuantity >= holder.selectedValue) {
                updateQuantity(stock.getId(), currentQuantity - holder.selectedValue, holder, false);
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.stock_value,
                R.layout.spinner_item_admin);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerDocument.setAdapter(adapter);

        holder.one.setOnClickListener(v -> {
            handleButtonClick(holder, holder.one);
            holder.selectedValue = 1;
        });

        holder.ten.setOnClickListener(v -> {
            handleButtonClick(holder, holder.ten);
            holder.selectedValue = 10;
        });

        holder.thousand.setOnClickListener(v -> {
            handleButtonClick(holder, holder.thousand);
            holder.selectedValue = 1000;
        });
    }

    private void handleButtonClick(viewholder holder, View clickedButton) {
        resetButtonBackgrounds(holder);
        clickedButton.setBackgroundResource(R.drawable.orange_button);
    }

    private void resetButtonBackgrounds(viewholder holder) {
        holder.one.setBackgroundResource(R.drawable.orange_button_light);
        holder.ten.setBackgroundResource(R.drawable.orange_button_light);
        holder.thousand.setBackgroundResource(R.drawable.orange_button_light);
    }


    @Override
    public int getItemCount() {
        return itemsStock.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView title, number,day;
        ImageView img,plus,minus;
        Spinner spinnerDocument;
        Button one,ten,thousand;
        int selectedValue;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.stockTitleAdminTxt);
            number = itemView.findViewById(R.id.stockNumberTxt);
            img = itemView.findViewById(R.id.stockAdminImg);
            plus=itemView.findViewById(R.id.stockPlusBtn);
            minus=itemView.findViewById(R.id.stockMinusBtn);
            day=itemView.findViewById(R.id.stockDay);
            spinnerDocument=itemView.findViewById(R.id.spinnerDoc);
            one=itemView.findViewById(R.id.stockOne);
            ten=itemView.findViewById(R.id.stockTen);
            thousand=itemView.findViewById(R.id.stockThousand);

        }
    }
    private void updateQuantity(int stockId, int newQuantity, viewholder holder, boolean increment) {
        UserService userService = Client.getService();

        userService.getStockByArticleId(stockId).enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Stock stock = response.body();
                    int oldQuantity = stock.getKolicina();

                    stock.setId(stock.getId());
                    stock.setArtikal(stock.getArtikal());
                    stock.setDokument_Id(stock.getDokument_Id());
                    stock.setKorisnik_Id(stock.getKorisnik_Id());
                    stock.setKolicina(newQuantity);

                    userService.updateStock(stock.getId(), stock).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                holder.number.setText(String.valueOf(newQuantity));

                                String currentDate = getCurrentDate();

                                // Pozivanje metoda za unos u Primke ili Izdatnice/Otpis
                                if (increment) {
                                    saveReceipt(stock.getId(), newQuantity - oldQuantity);
                                } else {
                                    saveAdjustment(stock.getId(), newQuantity - oldQuantity);
                                }
                            } else {
                                Toast.makeText(context, "Greška prilikom ažuriranja skladišta.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(context, "Greška prilikom provjere skladišta.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveReceipt(int stockId, int quantityAdded) {
        String currentDate = getCurrentDate(); // Format: "yyyy-MM-dd"
        UserService userService = Client.getService();

        userService.getReceiptsByStockIdAndDate(stockId, currentDate).enqueue(new Callback<List<Receipt>>() {
            @Override
            public void onResponse(Call<List<Receipt>> call, Response<List<Receipt>> response) {
                if (response.isSuccessful()) {
                    List<Receipt> existingReceipts = response.body();
                    if (existingReceipts != null && !existingReceipts.isEmpty()) {
                        Receipt existingReceipt = existingReceipts.get(0);
                        int newQuantity = existingReceipt.getKolicina() + quantityAdded;
                        existingReceipt.setKolicina(newQuantity);

                        userService.updateReceipt(existingReceipt.getId(), existingReceipt).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.d("saveReceipt", "Primka uspešno ažurirana.");
                                } else {
                                    Log.e("saveReceipt", "Greška prilikom ažuriranja primke: " + response.message());
                                    Toast.makeText(context, "Greška prilikom ažuriranja primke.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("saveReceipt", "Greška u komunikaciji sa serverom prilikom ažuriranja: " + t.getMessage());
                                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Primka ne postoji, kreiramo novu
                        Receipt newReceipt = new Receipt();
                        newReceipt.setArtikal_Id(stockId);
                        newReceipt.setKolicina(quantityAdded);
                        newReceipt.setDatum(currentDate);

                        userService.createReceipt(newReceipt).enqueue(new Callback<Receipt>() {
                            @Override
                            public void onResponse(Call<Receipt> call, Response<Receipt> response) {
                                if (response.isSuccessful()) {
                                    Log.d("saveReceipt", "Primka uspešno kreirana.");
                                } else {
                                    Log.e("saveReceipt", "Greška prilikom kreiranja primke: " + response.message());
                                    Toast.makeText(context, "Greška prilikom kreiranja primke.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Receipt> call, Throwable t) {
                                Log.e("saveReceipt", "Greška u komunikaciji sa serverom prilikom kreiranja: " + t.getMessage());
                                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.e("saveReceipt", "Greška prilikom dohvatanja primke: " + response.message());
                    Toast.makeText(context, "Greška prilikom dohvatanja primke.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Receipt>> call, Throwable t) {
                Log.e("saveReceipt", "Greška u komunikaciji sa serverom prilikom dohvatanja: " + t.getMessage());
                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAdjustment(int stockId, int quantityAdded) {
        String currentDate = getCurrentDate(); // Format: "yyyy-MM-dd"
        UserService userService = Client.getService();

        userService.getAdjustmentByStockIdAndDate(stockId, currentDate).enqueue(new Callback<List<Adjustment>>() {
            @Override
            public void onResponse(Call<List<Adjustment>> call, Response<List<Adjustment>> response) {
                if (response.isSuccessful()) {
                    List<Adjustment> existingAdjustments = response.body();
                    if (existingAdjustments != null && !existingAdjustments.isEmpty()) {
                        Adjustment existingAdjustment = existingAdjustments.get(0);
                        int newQuantity = existingAdjustment.getKolicina() + quantityAdded;
                        existingAdjustment.setKolicina(newQuantity);

                        userService.updateAdjustment(existingAdjustment.getId(), existingAdjustment).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.d("saveAdjustment", "Otpis uspešno ažurirana.");
                                } else {
                                    Log.e("saveAdjustment", "Greška prilikom ažuriranja otpis: " + response.message());
                                    Toast.makeText(context, "Greška prilikom ažuriranja otpis.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("saveAdjustment", "Greška u komunikaciji sa serverom prilikom ažuriranja: " + t.getMessage());
                                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Primka ne postoji, kreiramo novu
                        Adjustment newAdjustment = new Adjustment();
                        newAdjustment.setArtikal_Id(stockId);
                        newAdjustment.setKolicina(quantityAdded);
                        newAdjustment.setDatum(currentDate);

                        userService.createAdjustment(newAdjustment).enqueue(new Callback<Adjustment>() {
                            @Override
                            public void onResponse(Call<Adjustment> call, Response<Adjustment> response) {
                                if (response.isSuccessful()) {
                                    Log.d("saveAdjustment", "Primka uspešno kreirana.");
                                } else {
                                    Log.e("saveAdjustment", "Greška prilikom kreiranja otpis: " + response.message());
                                    Toast.makeText(context, "Greška prilikom kreiranja otpis.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Adjustment> call, Throwable t) {
                                Log.e("saveAdjustment", "Greška u komunikaciji sa serverom prilikom kreiranja: " + t.getMessage());
                                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.e("saveAdjustment", "Greška prilikom dohvatanja otpis: " + response.message());
                    Toast.makeText(context, "Greška prilikom dohvatanja otpis.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Adjustment>> call, Throwable t) {
                Log.e("saveAdjustment", "Greška u komunikaciji sa serverom prilikom dohvatanja: " + t.getMessage());
                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

}
