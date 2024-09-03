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

import com.example.onlinekonobar.Activity.Admin.DocumentDetail;
import com.example.onlinekonobar.Api.Adjustment;
import com.example.onlinekonobar.Api.Receipt;
import com.example.onlinekonobar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.viewholder> {
    private List<Receipt> receiptList;
    private List<Adjustment> adjustmentList;
    private String selectedDocument;
    private Context context;

    public DocumentListAdapter(List<Receipt>receiptList,List<Adjustment> adjustmentList,String selectedDocument,Context context){
        this.receiptList=receiptList;
        this.adjustmentList = adjustmentList;
        this.selectedDocument = selectedDocument;
        this.context=context;
    }
    @NonNull
    @Override
    public DocumentListAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_document, parent, false);
        return new DocumentListAdapter.viewholder(inflate);
    }
    @Override
    public void onBindViewHolder(viewholder holder, int position) {
        if (receiptList != null) {
            Receipt receipt = receiptList.get(position);
            holder.date.setText(convertDateFormat(receipt.getDatum() + ""));
            holder.itemView.setOnClickListener(v -> {
                // Create a new bundle and put the selected Receipt object in it
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_date", receipt.getDatum());
                bundle.putString("selected_document", selectedDocument);

                Log.d("Detail Inovice","Date "+bundle);

                DocumentDetail documentDetail = new DocumentDetail();
                documentDetail.setArguments(bundle);

                // Start the fragment transaction to display the new fragment
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentDocumentDetails, documentDetail);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // Show the fragment container if it is hidden
                View frameLayout = ((FragmentActivity) context).findViewById(R.id.fragmentDocumentDetails);
                frameLayout.setVisibility(View.VISIBLE);
            });
        } else if (adjustmentList != null) {
            Adjustment adjustment = adjustmentList.get(position);
            holder.date.setText(convertDateFormat(adjustment.getDatum() + ""));
            holder.itemView.setOnClickListener(v -> {
                // Create a new bundle and put the selected Receipt object in it
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_date", adjustment.getDatum());
                bundle.putString("selected_document", selectedDocument);

                Log.d("Detail Inovice","Date "+bundle);

                DocumentDetail documentDetail = new DocumentDetail();
                documentDetail.setArguments(bundle);

                // Start the fragment transaction to display the new fragment
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentDocumentDetails, documentDetail);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // Show the fragment container if it is hidden
                View frameLayout = ((FragmentActivity) context).findViewById(R.id.fragmentDocumentDetails);
                frameLayout.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (receiptList != null) {
            return receiptList.size();
        } else if (adjustmentList != null) {
            return adjustmentList.size();
        } else {
            return 0;
        }
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView date;
        ImageView orderBtn;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.documentListDate);
            orderBtn=itemView.findViewById(R.id.getDocument);
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
