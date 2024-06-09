package com.example.onlinekonobar.Activity.User;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinekonobar.Adapter.InvoiceUserAdapter;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceList extends Fragment {
    private RecyclerView.Adapter adapterOrder;
    private RecyclerView invoice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice_list, container, false);

        invoice = view.findViewById(R.id.userInvoiceRecycler);
        initList();
        return view;
    }

    public void initList() {
        UserService service = Client.getService();
        Call<ArrayList<Invoice>> call = service.getAllInvoice();
        call.enqueue(new Callback<ArrayList<Invoice>>() {
            @Override
            public void onResponse(Call<ArrayList<Invoice>> call, Response<ArrayList<Invoice>> response) {
                ArrayList<Invoice> invoiceList = response.body();
                if (invoiceList != null && !invoiceList.isEmpty()) {
                    invoice.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    adapterOrder = new InvoiceUserAdapter(invoiceList, getContext());
                    invoice.setAdapter(adapterOrder);
                } else {
                    Log.d("InvoiceList", "Invoice list is empty or null");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Invoice>> call, Throwable throwable) {
                Log.e("InvoiceList", "Error fetching invoices", throwable);
            }
        });
    }
}
