package com.example.onlinekonobar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Api.Category;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryAdapter.viewholder> {
    ArrayList<Category> items;
    Context context;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private CategoryClickListener categoryClickListener;


    public interface CategoryClickListener {
        void onCategoryClicked(int categoryId);
    }


    public CategoryAdapter(ArrayList<Category> items) {
        this.items = items;

    }
    public void setCategoryClickListener(CategoryClickListener listener) {
        this.categoryClickListener = listener;
    }


    @NonNull
    @Override
    public CategoryAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflat=LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category,parent,false);
        return new viewholder(inflat);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getNaziv());

        // Postavljanje boje teksta ovisno o tome je li trenutna stavka odabrana ili ne
        if (position == selectedPosition) {
            holder.titleTxt.setTextColor(ContextCompat.getColor(context, R.color.selectedTextColor));
            holder.titleTxt.setPaintFlags(holder.titleTxt.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        } else {
            holder.titleTxt.setTextColor(ContextCompat.getColor(context, R.color.defaultTextColor));
            holder.titleTxt.setPaintFlags(holder.titleTxt.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousSelectedPosition = selectedPosition;
                if (previousSelectedPosition == holder.getAdapterPosition()) {
                    selectedPosition = RecyclerView.NO_POSITION;
                } else {
                    selectedPosition = holder.getAdapterPosition();
                }
                notifyDataSetChanged();
                if (categoryClickListener != null) {
                    categoryClickListener.onCategoryClicked(items.get(position).getId());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends  RecyclerView.ViewHolder {
        TextView titleTxt;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.categoryTxt);
        }
    }
}
