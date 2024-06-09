package com.example.onlinekonobar.Activity.User;

import android.content.Context;
import android.os.Bundle;

import androidx.arch.core.internal.SafeIterableMap;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.example.onlinekonobar.Adapter.InvoiceDetailAdapter;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailInvoice extends Fragment {
    private RecyclerView.Adapter adapterInvoice;
    private Invoice invoiceObject;
    TextView orderNumber,date,status,subtotal,vat,total;
    Button storn;
    int invoiceId;
    Context context;
    RecyclerView itemInvoice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_invoice, container, false);

        orderNumber=view.findViewById(R.id.orderNumberInvoiceTxt);
        date=view.findViewById(R.id.dateOrderInvocieTxt);
        status=view.findViewById(R.id.ststusOrderInvoiceTxt);
        subtotal=view.findViewById(R.id.subtotalInvoiceTxt);
        vat=view.findViewById(R.id.vatInvoiceTxt);
        total=view.findViewById(R.id.totalInvoiceTxt);
        itemInvoice=view.findViewById(R.id.detailInvoiceRecycler);
        storn=view.findViewById(R.id.stornUserBtn);
        if (getArguments() != null) {
            invoiceObject = (Invoice) getArguments().getSerializable("selected_invoice");
            setVariable();
            invoiceId= invoiceObject.getId();
            Log.d("Detail Inovice","Inovice id "+invoiceId);
            context=getContext();
        }
        storn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = Client.getService();
                Call<Invoice> callInvoice = userService.getInvoiceById(invoiceId);
                callInvoice.enqueue(new Callback<Invoice>() {
                    @Override
                    public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                        Invoice invoice= response.body();
                        if (invoice != null) {
                            invoice.setId(invoice.getId());
                            invoice.setDokument_Id(invoice.getDokument_Id());
                            invoice.setBroj_Racuna(invoice.getBroj_Racuna());
                            invoice.setUkupan_Iznos(invoice.getUkupan_Iznos());
                            invoice.setDatum(invoice.getDatum());
                            invoice.setKorisnik_Id(invoice.getKorisnik_Id());
                            invoice.setStatus("Stornirano");
                            userService.stornInvoice(invoiceId,invoice).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, "Racun je storniran", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Greška prilikom storniranja narudžbe.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable throwable) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onFailure(Call<Invoice> call, Throwable throwable) {

                    }
                });
            }
        });

        initList();
        return view;
    }
    private void initList() {
        UserService service = Client.getService();
        Call<ArrayList<Article>> callArticles = service.getAllArticles();
        Call<ArrayList<Item>> callItems = service.getAllItems();
        Call<ArrayList<Customize>> callCustomize = service.getAllCustomize();
        Call<Invoice> callInvoice = service.getInvoiceById(invoiceId);

        callInvoice.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Invoice invoice = response.body();
                    if (invoice != null) {
                        callItems.enqueue(new Callback<ArrayList<Item>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ArrayList<Item> allItems = response.body();
                                    ArrayList<Item> invoiceItems = new ArrayList<>();

                                    if (allItems != null && !allItems.isEmpty()) {
                                        for (Item item : allItems) {
                                            if (item.getOrder_Id() != null) {
                                                if (item.getOrder_Id().equals(invoice.getBroj_Racuna())) {
                                                    invoiceItems.add(item);
                                                }
                                            }
                                        }
                                        callCustomize.enqueue(new Callback<ArrayList<Customize>>() {
                                            @Override
                                            public void onResponse(Call<ArrayList<Customize>> call, Response<ArrayList<Customize>> response) {
                                                if (response.isSuccessful() && response.body() != null) {
                                                    ArrayList<Customize> allCustomize = response.body();
                                                    ArrayList<Customize> invoiceCustomize = new ArrayList<>();

                                                    if (allCustomize != null && !allCustomize.isEmpty()) {
                                                        for (Item item : invoiceItems) {
                                                            for (Customize customize : allCustomize) {
                                                                if (customize.getId() == item.getDodatak()) {
                                                                    invoiceCustomize.add(customize);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    callArticles.enqueue(new Callback<ArrayList<Article>>() {
                                                        @Override
                                                        public void onResponse(Call<ArrayList<Article>> call, Response<ArrayList<Article>> response) {
                                                            if (response.isSuccessful() && response.body() != null) {
                                                                ArrayList<Article> allArticles = response.body();
                                                                ArrayList<Article> invoiceArticles = new ArrayList<>();

                                                                if (allArticles != null && !allArticles.isEmpty()) {
                                                                    for (Item item : invoiceItems) {
                                                                        for (Article article : allArticles) {
                                                                            if (article.getId() == item.getArtikal_Id()) {
                                                                                invoiceArticles.add(article);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                itemInvoice.setLayoutManager(new GridLayoutManager(getContext(), 1));
                                                                adapterInvoice = new InvoiceDetailAdapter(invoiceArticles, invoiceCustomize, invoiceItems,getContext());
                                                                itemInvoice.setAdapter(adapterInvoice);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ArrayList<Article>> call, Throwable throwable) {
                                                            Log.e("initList", "Error fetching articles: " + throwable.getMessage());
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ArrayList<Customize>> call, Throwable throwable) {
                                                Log.e("initList", "Error fetching customize: " + throwable.getMessage());
                                            }
                                        });
                                    }
                                } else {
                                    Log.e("initList", "Error in items response: " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Item>> call, Throwable throwable) {
                                Log.e("initList", "Error fetching items: " + throwable.getMessage());
                            }
                        });
                    }
                } else {
                    Log.e("initList", "Error in invoice response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Invoice> call, Throwable throwable) {
                Log.e("initList", "Error fetching invoice: " + throwable.getMessage());
            }
        });
    }

    private void setVariable()
    {
        if(invoiceObject !=null) {
            orderNumber.setText(
                    String.valueOf(invoiceObject.getBroj_Racuna()));
            date.setText(String.valueOf(convertDateFormat(invoiceObject.getDatum())));
            status.setText(String.valueOf(invoiceObject.getStatus()));
            total.setText(String.format("%.2f", invoiceObject.getUkupan_Iznos())+"€");
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