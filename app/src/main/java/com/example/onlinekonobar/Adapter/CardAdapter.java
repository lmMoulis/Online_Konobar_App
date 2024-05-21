package com.example.onlinekonobar.Adapter;

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
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.viewholder> {
    ArrayList<Article> items;
    ArrayList<Customize>itemsCustomize;
    private Context context;

    public CardAdapter(ArrayList<Article> items, Context context) {
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public CardAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_card, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.viewholder holder, int position) {
        Article article = items.get(position);
        Customize customize = itemsCustomize.get(position);

        holder.title.setText(article.getNaziv());
        holder.cartPrice.setText(article.getCijena()+"€");

        Glide.with(context)
                .load(items.get(position).getSlika())
                .transform(new FitCenter(),new RoundedCorners(20))
                .into(holder.img);


    }

    @Override
    public int getItemCount() {return items.size();}
    public class viewholder extends RecyclerView.ViewHolder{
        TextView title,custome,cartPrice,number;
        ImageView img;
        public viewholder(@NonNull View itemView)
        {
            super(itemView);
            title=itemView.findViewById(R.id.cartTitleTxt);
            custome=itemView.findViewById(R.id.customeCartTxt);
            cartPrice=itemView.findViewById(R.id.cartPriceTxt);
            number=itemView.findViewById(R.id.numberTxt);
            img=itemView.findViewById(R.id.cartImg);
        }
    }
}
