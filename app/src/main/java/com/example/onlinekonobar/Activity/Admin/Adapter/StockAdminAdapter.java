package com.example.onlinekonobar.Activity.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

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
        holder.count.setText(String.valueOf(stock.getKolicina()));
        Glide.with(context)
                .load(items.get(position).getSlika())
                .transform(new FitCenter(),new RoundedCorners(20))
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView title, count;
        ImageView img;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.stockTitleAdminTxt);
            count = itemView.findViewById(R.id.stockNumberTxt);
            img = itemView.findViewById(R.id.stockAdminImg);
        }
    }
}
