package com.example.onlinekonobar.Activity.User.Adapter;

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

public class InvoiceDetailAdapter extends RecyclerView.Adapter<InvoiceDetailAdapter.viewholder>{
    private ArrayList<Article> articelItems;
    private  ArrayList<Customize>customizesItems;
    private ArrayList<Item>items;
    private Context context;

    public InvoiceDetailAdapter(ArrayList<Article> articelItems,ArrayList<Customize>customizesItems,ArrayList<Item>items, Context context) {
        this.articelItems = articelItems;
        this.customizesItems=customizesItems;
        this.items=items;
        this.context = context;
    }
    @NonNull
    @Override
    public InvoiceDetailAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_invoice_details, parent, false);
        return new InvoiceDetailAdapter.viewholder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull InvoiceDetailAdapter.viewholder holder, int position) {
        Article article = articelItems.get(position);
        Customize customize = customizesItems.get(position);
        Item item= items.get(position);
        holder.title.setText(String.valueOf(article.getNaziv()));
        holder.custome.setText(String.valueOf(customize.getNaziv()));
        holder.quantity.setText(String.valueOf(item.getKolicina()));
        holder.price.setText(String.format("%.2f",item.getKolicina()*article.getCijena())+"€");

        Glide.with(context)
                .load(articelItems.get(position).getSlika())
                .transform(new FitCenter(),new RoundedCorners(20))
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return articelItems.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView title, custome,quantity, price;
        ImageView img;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.invoiceTitleTxt);
            custome=itemView.findViewById(R.id.customeInvoiceTxt);
            quantity=itemView.findViewById(R.id.quantityInvoiceTxt);
            price=itemView.findViewById(R.id.invoicePriceTxt);
            img=itemView.findViewById(R.id.invoiceImg);

        }
    }
}
