package com.example.onlinekonobar.Adapter;

import android.content.Context;
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


    public CategoryAdapter(ArrayList<Category> items) {
        this.items = items;
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

        // Dodajte slušača klikova na stavku kako biste pratili odabir
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Postavite novu poziciju odabira i obavijestite adapter o promjeni
                int previousSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
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
