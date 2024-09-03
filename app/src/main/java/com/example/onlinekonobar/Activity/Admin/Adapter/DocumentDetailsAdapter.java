package com.example.onlinekonobar.Activity.Admin.Adapter;

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
import com.example.onlinekonobar.Api.Adjustment;
import com.example.onlinekonobar.Api.Receipt;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.R;

import java.util.List;

public class DocumentDetailsAdapter extends RecyclerView.Adapter<DocumentDetailsAdapter.viewholder> {
    private List<Receipt> receiptList;
    private List<Adjustment>adjustmentList;
    private List<Stock> stockList;
    private String selectedDocument;
    private Context context;
    public DocumentDetailsAdapter(List<Receipt> receiptList,List<Adjustment>adjustmentList,List<Stock>stockList,String selectedDocument, Context context) {
        this.receiptList = receiptList;
        this.adjustmentList=adjustmentList;
        this.stockList=stockList;
        this.selectedDocument=selectedDocument;
        this.context = context;
    }
    @NonNull
    @Override
    public DocumentDetailsAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_details_document, parent, false);
        return new DocumentDetailsAdapter.viewholder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull DocumentDetailsAdapter.viewholder holder, int position) {

        Stock stock = stockList.get(position);

        if (selectedDocument.equals("Primke")) {
            Receipt receipt = receiptList.get(position);
            holder.title.setText(String.valueOf(stock.getArtikal()));
            holder.quantity.setText(String.valueOf(receipt.getKolicina()));
            Glide.with(context)
                    .load(stockList.get(position).getSlike())
                    .transform(new FitCenter(),new RoundedCorners(20))
                    .into(holder.img);
        } else if (selectedDocument.equals("Otpis")) {
            Adjustment adjustment = adjustmentList.get(position);
            holder.title.setText(String.valueOf(stock.getArtikal()));
            holder.quantity.setText(String.valueOf(adjustment.getKolicina()));
            Glide.with(context)
                    .load(stockList.get(position).getSlike())
                    .transform(new FitCenter(),new RoundedCorners(20))
                    .into(holder.img);
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
    public static class viewholder extends RecyclerView.ViewHolder{
        TextView title, quantity;
        ImageView img;
        public viewholder(@NonNull View itemView){

            super(itemView);
            title=itemView.findViewById(R.id.documentTitleTxt);
            quantity=itemView.findViewById(R.id.quantityIDocumentTxt);
            img=itemView.findViewById(R.id.documentImg);

        }

    }
}
