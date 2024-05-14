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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

public class ArticleUserAdapter extends RecyclerView.Adapter<ArticleUserAdapter.viewholder> {
    ArrayList<Article> items;
    Context context;

    public ArticleUserAdapter(ArrayList<Article> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ArticleUserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_article,parent,false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleUserAdapter.viewholder holder, int position) {
        holder.title.setText(items.get(position).getNaziv());
        holder.price.setText(items.get(position).getCijena()+"€");
        Glide.with(context)
                .load(items.get(position).getSlika())
                .transform(new CenterCrop(),new RoundedCorners(20))
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public class viewholder  extends  RecyclerView.ViewHolder{
        TextView title,price,addBtn;
        ImageView img;
        public viewholder(@NonNull View itemView){
            super(itemView);

            title=itemView.findViewById(R.id.userTitleTxt);
            price=itemView.findViewById(R.id.userPriceTxt);
            addBtn=itemView.findViewById(R.id.userAddBtn);
            img=itemView.findViewById(R.id.userImg);
        }
    }
}
