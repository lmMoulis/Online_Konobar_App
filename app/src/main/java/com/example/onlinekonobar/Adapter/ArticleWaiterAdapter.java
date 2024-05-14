package com.example.onlinekonobar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

public class ArticleWaiterAdapter extends RecyclerView.Adapter<ArticleWaiterAdapter.viewholder> {
    ArrayList<Article>items;
    Context context;

    public ArticleWaiterAdapter(ArrayList<Article> items){this.items=items;}


    @NonNull
    @Override
    public ArticleWaiterAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate= LayoutInflater.from(context).inflate(R.layout.viewholder_list_waiter_article,parent,false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleWaiterAdapter.viewholder holder, int position) {
        holder.title.setText(items.get(position).getNaziv());
    }

    @Override
    public int getItemCount() {return items.size();}
    public class viewholder extends RecyclerView.ViewHolder{
        TextView title;
        public viewholder(@NonNull View itemView)
        {
            super(itemView);
            title=itemView.findViewById(R.id.waiterTitleTxt);
        }
    }
}
