package com.example.onlinekonobar.Adapter;

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
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.ManagementCart;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

public class CartWaiterAdapter extends RecyclerView.Adapter<CartWaiterAdapter.viewholder> {
    ArrayList<Article> items;
    ArrayList<Customize> itemsCustomize;
    private Context context;
    ManagementCart managementCart;
    private Runnable emptyStateChecker;

    public CartWaiterAdapter(ArrayList<Article> items, ArrayList<Customize>itemsCustomize , Context context, ManagementCart managementCart, Runnable emptyStateChecker) {
        this.items = items;
        this.context = context;
        this.itemsCustomize=itemsCustomize;
        this.managementCart=managementCart;
        this.emptyStateChecker=emptyStateChecker;
    }
    public CartWaiterAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_waiter_cart, parent, false);

        return new CartWaiterAdapter.viewholder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CartWaiterAdapter.viewholder holder, int position) {
        Article article = items.get(position);
        Customize customize = itemsCustomize.get(position);
        holder.title.setText(article.getNaziv());
        holder.custome.setText(customize.getNaziv());
        int quantity = managementCart.getItemQuantity(article.getId(), customize.getId());
        holder.number.setText(String.valueOf(quantity));
        holder.cartPrice.setText(String.format("%.2f",article.getCijena()*quantity)+"€");


        Glide.with(context)
                .load(items.get(position).getSlika())
                .transform(new FitCenter(),new RoundedCorners(20))
                .into(holder.img);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.deleteArticle(article.getId());
                items.remove(position);
                itemsCustomize.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
                emptyStateChecker.run();
            }
        });
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int currentQuantity = Integer.parseInt(holder.number.getText().toString());
                managementCart.checkStockAndUpdateQuantity(article.getId(), true, new ManagementCart.StockUpdateCallback() {
                    @Override
                    public void onStockUpdate(boolean success) {
                        if (success) {
                            holder.number.setText(String.valueOf(currentQuantity + 1));
                            holder.cartPrice.setText(String.format("%.2f", article.getCijena() * (currentQuantity + 1)) + "€");
                        } else {
                            Toast.makeText(context, "Nedovoljno artikala na skladištu.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.number.getText().toString());
                if (currentQuantity > 1) {
                    currentQuantity--;
                    holder.number.setText(String.valueOf(currentQuantity));
                    managementCart.decrementQuantity(article.getId());
                    holder.cartPrice.setText(String.format("%.2f", article.getCijena() * currentQuantity) + "€");
                }
            }
        });
    }
    @Override
    public int getItemCount() {return items.size();}
    public class viewholder extends RecyclerView.ViewHolder{
        TextView title,custome,cartPrice,number;
        ImageView img,delete,plus,minus;

        public viewholder(@NonNull View itemView)
        {
            super(itemView);
            title=itemView.findViewById(R.id.cartTitleWaiterTxt);
            custome=itemView.findViewById(R.id.customeCartWaiterTxt);
            cartPrice=itemView.findViewById(R.id.cartPriceWaiterTxt);
            number=itemView.findViewById(R.id.numberWaiterTxt);
            img=itemView.findViewById(R.id.cartWaiterImg);
            delete=itemView.findViewById(R.id.cartDeleteWaiterBtn);
            plus=itemView.findViewById(R.id.plusWaiterBtn);
            minus=itemView.findViewById(R.id.minusWaiterBtn);
        }
    }

}

