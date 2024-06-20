package com.example.onlinekonobar.Activity.Admin.Adapter;

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

import com.example.onlinekonobar.Activity.Admin.InvoiceDetailAdmin;
import com.example.onlinekonobar.Activity.Waiter.DetailInvoice;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InvoiceListAdminAdapter extends RecyclerView.Adapter<InvoiceListAdminAdapter.viewholder> {

    private ArrayList<Invoice> invoicesItems;
    private ArrayList<User>usersItems;
    private Context context;

    public InvoiceListAdminAdapter(ArrayList<Invoice> invoicesItems,ArrayList<User>usersItems, Context context) {
        this.invoicesItems = invoicesItems;
        this.usersItems=usersItems;
        this.context = context;
    }

    @NonNull
    @Override
    public InvoiceListAdminAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_order_admin, parent, false);
        return new InvoiceListAdminAdapter.viewholder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull InvoiceListAdminAdapter.viewholder holder, int position) {
        Invoice invoice = invoicesItems.get(position);
        User users=usersItems.get(position);
        holder.name.setText(users.getIme()+" "+  users.getPrezime());
        String brojRacuna = invoice.getBroj_Racuna();
        int lastDashIndex = brojRacuna.lastIndexOf('-');
        if (lastDashIndex != -1) {
            String brojRacunaNovi = brojRacuna.substring(lastDashIndex + 1);
            holder.order.setText(brojRacunaNovi);
        }

        holder.date.setText(convertDateFormat(invoice.getDatum()));
        holder.price.setText(invoice.getUkupan_Iznos()+" €");

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selected_invoice", invoice);
            InvoiceDetailAdmin invocieDetail = new InvoiceDetailAdmin();
            invocieDetail.setArguments(bundle);

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentInvoiceDetailsAdmin, invocieDetail);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            View frameLayout = ((FragmentActivity) context).findViewById(R.id.fragmentInvoiceDetailsAdmin);
            frameLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return invoicesItems.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView name,order, date, price;
        ImageView orderBtn;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.nameOrderAdminTxt);
            order = itemView.findViewById(R.id.orderNumberAdminTxt);
            date = itemView.findViewById(R.id.orderDateAdminTxt);
            price = itemView.findViewById(R.id.orderPriceAdminTxt);
            orderBtn = itemView.findViewById(R.id.getOrderAdmin);

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