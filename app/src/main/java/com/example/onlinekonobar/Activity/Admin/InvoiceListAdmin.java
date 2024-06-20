package com.example.onlinekonobar.Activity.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onlinekonobar.Activity.Admin.Adapter.InvoiceListAdminAdapter;
import com.example.onlinekonobar.Activity.Waiter.Adapter.InvoiceWaiterAdapter;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceListAdmin extends Fragment {

    private RecyclerView.Adapter adapterOrder;
    private RecyclerView invoice;
    TextView empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice_list_admin, container, false);

        invoice = view.findViewById(R.id.adminInvoiceRecycler);
        empty=view.findViewById(R.id.emptyInvoiceListAdmin);

        initList();
        return view;
    }
    public void initList() {
        UserService service = Client.getService();

        // Retrieve invoices
        Call<ArrayList<Invoice>> call = service.getAllInvoice();
        call.enqueue(new Callback<ArrayList<Invoice>>() {
            @Override
            public void onResponse(Call<ArrayList<Invoice>> call, Response<ArrayList<Invoice>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Invoice> invoiceList = response.body();
                    ArrayList<Invoice> invoiceItems = new ArrayList<>();

                    if (invoiceList != null && !invoiceList.isEmpty()) {
                        invoiceItems.addAll(invoiceList); // Add all invoices to invoiceItems list

                        // Retrieve users
                        Call<ArrayList<User>> callUser = service.getAllUsers();
                        callUser.enqueue(new Callback<ArrayList<User>>() {
                            @Override
                            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ArrayList<User> allUsers = response.body();
                                    ArrayList<User> usersItems = new ArrayList<>();

                                    if (allUsers != null && !allUsers.isEmpty()) {
                                        // Match users with invoices
                                        for (Invoice invoice : invoiceItems) {
                                            for (User user : allUsers) {
                                                if (invoice.getKorisnik_Id() == user.getId()) {
                                                    usersItems.add(user); // Add matched user to usersItems list
                                                }
                                            }
                                        }

                                        // Set up RecyclerView adapter
                                        invoice.setLayoutManager(new GridLayoutManager(getContext(), 1));
                                        adapterOrder = new InvoiceListAdminAdapter(invoiceItems, usersItems, getContext());
                                        invoice.setAdapter(adapterOrder);
                                        checkEmptyState();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<User>> call, Throwable throwable) {
                                Log.e("InvoiceList", "Error fetching users", throwable);
                            }
                        });
                    } else {
                        Log.d("InvoiceList", "Invoice list is empty or null");
                        checkEmptyState();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Invoice>> call, Throwable throwable) {
                Log.e("InvoiceList", "Error fetching invoices", throwable);
            }
        });
    }

    private void checkEmptyState() {
        if (adapterOrder != null && adapterOrder.getItemCount() != 0) {
            invoice.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
            invoice.setVisibility(View.GONE);
        }
    }
}