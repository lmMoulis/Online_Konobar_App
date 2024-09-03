package com.example.onlinekonobar.Activity.Admin;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinekonobar.Activity.Admin.Adapter.DocumentDetailsAdapter;
import com.example.onlinekonobar.Activity.Admin.Adapter.DocumentListAdapter;
import com.example.onlinekonobar.Api.Adjustment;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Receipt;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentDetail extends Fragment {

    private String date;
    private String selectedDocument;
    private RecyclerView itemsList;
    private DocumentDetailsAdapter adapterDocument;
    private List<Stock> stockList = new ArrayList<>();
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_detail, container, false);

        itemsList = view.findViewById(R.id.recyclerViewListDocutment);
        if (getArguments() != null) {
            date = getArguments().getString("selected_date");
            selectedDocument=getArguments().getString("selected_document");
            Log.d("DocumentDetail", "Received date: " + date);
        }
        if (selectedDocument.equals("Primke")) {
            initListReceipt();
        } else if (selectedDocument.equals("Otpis")) {
            initListAdjustment();
        }

        return view;
    }
    public void initListReceipt() {
        UserService service = Client.getService();
        Call<ArrayList<Receipt>> callReceipts = service.getReceiptsByDate(date);
        callReceipts.enqueue(new Callback<ArrayList<Receipt>>() {
            @Override
            public void onResponse(Call<ArrayList<Receipt>> call, Response<ArrayList<Receipt>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Receipt> receiptList = response.body();
                    ArrayList<Receipt> receiptItems = new ArrayList<>();
                    if (receiptList != null && !receiptList.isEmpty()) {
                        receiptItems.addAll(receiptList);
                        for (Receipt receipt : receiptItems) {
                            Call<Stock> callStock = service.getStockDetails(receipt.getArtikal_Id());
                            callStock.enqueue(new Callback<Stock>() {
                                @Override
                                public void onResponse(Call<Stock> call, Response<Stock> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        stockList.add(response.body());

                                        // Provera da li su svi podaci dodani pre postavljanja u adapter
                                        if (stockList.size() == receiptItems.size()) {
                                            itemsList.setLayoutManager(new GridLayoutManager(getContext(), 1));
                                            adapterDocument = new DocumentDetailsAdapter(receiptItems, null,stockList,selectedDocument,getContext());
                                            itemsList.setAdapter(adapterDocument);
                                            Log.d("DocumentDetail", "All stocks loaded and updated in the adapter");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Stock> call, Throwable throwable) {
                                    Log.e("DocumentDetail", "Error fetching stock details", throwable);
                                }
                            });
                        }
                    } else {
                        Log.d("DocumentDetail", "Receipt list is empty or null");
                    }
                } else {
                    Log.e("DocumentDetail", "Error fetching receipts");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Receipt>> call, Throwable throwable) {
                Log.e("DocumentDetail", "Error fetching receipts", throwable);
            }
        });
    }
    public void initListAdjustment() {
        UserService service = Client.getService();
        Call<ArrayList<Adjustment>> callAdjustment = service.getAdjustmentByDate(date);
        callAdjustment.enqueue(new Callback<ArrayList<Adjustment>>() {
            @Override
            public void onResponse(Call<ArrayList<Adjustment>> call, Response<ArrayList<Adjustment>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Adjustment> adjustmentList = response.body();
                    ArrayList<Adjustment> adjustmentItems = new ArrayList<>();
                    if (adjustmentList != null && !adjustmentList.isEmpty()) {
                        adjustmentItems.addAll(adjustmentList);
                        for (Adjustment adjustment : adjustmentItems) {
                            Call<Stock> callStock = service.getStockDetails(adjustment.getArtikal_Id());
                            callStock.enqueue(new Callback<Stock>() {
                                @Override
                                public void onResponse(Call<Stock> call, Response<Stock> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        stockList.add(response.body());

                                        // Provera da li su svi podaci dodani pre postavljanja u adapter
                                        if (stockList.size() == adjustmentItems.size()) {
                                            itemsList.setLayoutManager(new GridLayoutManager(getContext(), 1));
                                            adapterDocument = new DocumentDetailsAdapter(null,adjustmentItems, stockList,selectedDocument ,getContext());
                                            itemsList.setAdapter(adapterDocument);
                                            Log.d("DocumentDetail", "All stocks loaded and updated in the adapter");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Stock> call, Throwable throwable) {
                                    Log.e("DocumentDetail", "Error fetching stock details", throwable);
                                }
                            });
                        }
                    } else {
                        Log.d("DocumentDetail", "Receipt list is empty or null");
                    }
                } else {
                    Log.e("DocumentDetail", "Error fetching receipts");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Adjustment>> call, Throwable throwable) {
                Log.e("DocumentDetail", "Error fetching receipts", throwable);
            }
        });
    }

}

