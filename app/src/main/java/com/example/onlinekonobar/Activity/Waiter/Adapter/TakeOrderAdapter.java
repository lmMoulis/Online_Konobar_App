package com.example.onlinekonobar.Activity.Waiter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Activity.Waiter.DetailInvoice;
import com.example.onlinekonobar.Api.Category;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakeOrderAdapter extends RecyclerView.Adapter<TakeOrderAdapter.viewholder>{

    private ArrayList<Invoice> invoicesItems;
    private Context context;

    public TakeOrderAdapter(ArrayList<Invoice> invoicesItems, Context context) {
        this.invoicesItems = invoicesItems;
        this.context = context;
    }

    @NonNull
    @Override
    public TakeOrderAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_take_order_waiter, parent, false);
        return new TakeOrderAdapter.viewholder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull TakeOrderAdapter.viewholder holder, int position) {
        Invoice invoice = invoicesItems.get(position);
        holder.order.setText(String.valueOf(invoice.getId()));
        holder.price.setText(invoice.getUkupan_Iznos()+" €");

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selected_invoice", invoice);

            // Kreiraj AlertDialog
            new AlertDialog.Builder(context)
                    .setTitle("Potvrda narudžbe")
                    .setMessage("Želite li preuzeti narudžbu?")
                    .setPositiveButton("Da", (dialog, which) -> {
                        invoice.setDokument_Id(invoice.getDokument_Id());
                        invoice.setBroj_Racuna(invoice.getBroj_Racuna());
                        invoice.setUkupan_Iznos(invoice.getUkupan_Iznos());
                        invoice.setDatum(invoice.getDatum());
                        invoice.setKorisnik_Id(invoice.getKorisnik_Id());
                        invoice.setStatus(invoice.getStatus());
                        invoice.setKonobar_Id(invoice.getKonobar_Id());
                        invoice.setPreuzeto(true);

                        UserService userService = Client.getService();
                        Call<Void> call = userService.putNewElementInvoice(invoice.getId(), invoice);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Narudžba je uspješno preuzeta", Toast.LENGTH_SHORT).show();
                                    invoicesItems.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, invoicesItems.size());
                                } else {
                                    Toast.makeText(context, "Greška prilikom preuzimanja", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, "Greška u komunikaciji sa serverom", Toast.LENGTH_SHORT).show();
                                Log.e("TakeOrderAdapter", "Greška u komunikaciji sa serverom", t);
                            }
                        });

                    })
                    .setNegativeButton("Ne", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });
    }


    @Override
    public int getItemCount() {
        return invoicesItems.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView order, price;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            order = itemView.findViewById(R.id.takeOrderNumberTxt);
            price = itemView.findViewById(R.id.takeOrderPriceTxt);


        }
    }

}
