package com.example.onlinekonobar.Activity.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.onlinekonobar.Activity.User.Adapter.ManagementCart;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockAdminAdapter extends RecyclerView.Adapter<StockAdminAdapter.viewholder> {
    private ArrayList<Article> items;
    private ArrayList<Stock> itemsStock;
    private Context context;
    private int idUser;

    public StockAdminAdapter(ArrayList<Article> items, ArrayList<Stock> itemsStock, Context context, int idUser) {
        this.items = items;
        this.itemsStock = itemsStock;
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
        Article article = items.get(position);
        Stock stock = itemsStock.get(position);
        holder.title.setText(article.getNaziv());
        holder.number.setText(String.valueOf(stock.getKolicina()));
        Glide.with(context)
                .load(items.get(position).getSlika())
                .transform(new FitCenter(),new RoundedCorners(20))
                .into(holder.img);

        holder.plus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.number.getText().toString());
            updateQuantity(stock.getId(), currentQuantity + 1, holder, true);
        });

        holder.minus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.number.getText().toString());
            if (currentQuantity > 0) {
                updateQuantity(stock.getId(), currentQuantity - 1, holder, false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView title, number;
        ImageView img,plus,minus;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.stockTitleAdminTxt);
            number = itemView.findViewById(R.id.stockNumberTxt);
            img = itemView.findViewById(R.id.stockAdminImg);
            plus=itemView.findViewById(R.id.stockPlusBtn);
            minus=itemView.findViewById(R.id.stockMinusBtn);
        }
    }
    private void updateQuantity(int stockId, int newQuantity, viewholder holder, boolean increment) {
        UserService userService=Client.getService();
        userService.getStockByArticleId(stockId).enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Stock stock = response.body();

                        stock.setId(stock.getId());
                        stock.setArtikal_Id(stock.getArtikal_Id());
                        stock.setDokument_Id(stock.getDokument_Id());
                        stock.setKorisnik_Id(stock.getKorisnik_Id());
                        stock.setKolicina(newQuantity);
                        userService.updateStock(stock.getId(), stock).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    holder.number.setText(String.valueOf(newQuantity));
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
}
