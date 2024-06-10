package com.example.onlinekonobar.Activity.User.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Activity.User.DetailInvoice;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InvoiceUserAdapter extends RecyclerView.Adapter<InvoiceUserAdapter.viewholder> {
    private ArrayList<Invoice> invoicesItems;
    private Context context;

    public InvoiceUserAdapter(ArrayList<Invoice> invoicesItems, Context context) {
        this.invoicesItems = invoicesItems;
        this.context = context;

    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_order, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Invoice invoice = invoicesItems.get(position);
        holder.order.setText(String.valueOf(invoice.getId()));
        holder.date.setText(convertDateFormat(invoice.getDatum()));
        holder.price.setText(invoice.getUkupan_Iznos()+" €");

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selected_invoice", invoice);
            DetailInvoice detailFragment = new DetailInvoice();
            detailFragment.setArguments(bundle);

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentDetailInvoiceUser, detailFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            View frameLayout = ((FragmentActivity) context).findViewById(R.id.fragmentDetailInvoiceUser);
            frameLayout.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public int getItemCount() {
        return invoicesItems.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView order, date, price;
        ImageView orderBtn;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            order = itemView.findViewById(R.id.orderNumberTxt);
            date = itemView.findViewById(R.id.orderDateTxt);
            price = itemView.findViewById(R.id.orderPriceTxt);
            orderBtn = itemView.findViewById(R.id.getOrder);

        }
    }
    public String convertDateFormat(String date) {
        String newDateFormat = "";
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Log.d("convertDateFormat: ","Datum"+targetFormat);
        try {
            Date originalDate = originalFormat.parse(date);
            newDateFormat = targetFormat.format(originalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateFormat;
    }
}
