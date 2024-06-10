package com.example.onlinekonobar.Activity.User.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

public class CustomizeUserAdapter extends RecyclerView.Adapter<CustomizeUserAdapter.viewholder> {
    Context context;
    ArrayList<Customize>items;
    private int selectedPosition = -1;
    public CustomizeUserAdapter(ArrayList<Customize>items){
        this.items=items;
    }
    @NonNull
    @Override
    public CustomizeUserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       context= parent.getContext();
       View inflat= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_custom_drink,parent,false);
        return new viewholder(inflat);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomizeUserAdapter.viewholder holder, int position) {
        holder.titleCustomize.setText(items.get(position).getNaziv());

        holder.titleCustomize.setChecked(position == selectedPosition);
        holder.titleCustomize.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    public Customize getSelectedCustomize() {
        if (selectedPosition != -1) {
            return items.get(selectedPosition);
        }
        return null;
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public class viewholder extends RecyclerView.ViewHolder{
        RadioButton titleCustomize;
        Button save;
        public viewholder(@NonNull View itemView){
            super(itemView);
            titleCustomize=itemView.findViewById(R.id.titleCustomizeTxt);

            save=itemView.findViewById(R.id.saveBtn);
        }
    }
}
