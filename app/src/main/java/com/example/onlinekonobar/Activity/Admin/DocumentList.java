package com.example.onlinekonobar.Activity.Admin;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.onlinekonobar.Activity.Admin.Adapter.DocumentListAdapter;
import com.example.onlinekonobar.Activity.Admin.Adapter.InvoiceListAdminAdapter;
import com.example.onlinekonobar.Api.Adjustment;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.Receipt;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentList extends Fragment {
    TextView datum;
    String selectedDateString;
    private RecyclerView.Adapter adapterDocument;
    SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd. MMMM yyyy.", new Locale("hr", "HR"));
    SimpleDateFormat comparisonDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Spinner spinnerDay,spinnerDocument;
    RecyclerView documentList;
    String selectedDocument;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_document_list, container, false);

        spinnerDocument=view.findViewById(R.id.spinnerSelectDocument);
        documentList=view.findViewById(R.id.documentListView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.stock_value,
                R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocument.setAdapter(adapter);

        spinnerDocument.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDocument = parent.getItemAtPosition(position).toString();


                if (selectedDocument.equals("Primke")) {
                    initListReceipt();
                } else if (selectedDocument.equals("Otpis")) {
                    initListAdjustment();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }
    private List<Receipt> filterReceiptsByDate(List<Receipt> receipts) {
        Map<String, Receipt> dateMap = new HashMap<>();


        for (Receipt receipt : receipts) {
            // Pretpostavljam da `getDate()` vraća datum u formatu koji možete koristiti kao ključ
            String date = receipt.getDatum();
            if (!dateMap.containsKey(date)) {
                dateMap.put(date, receipt);
            }
        }

        return new ArrayList<>(dateMap.values());
    }

    public void initListReceipt() {
        UserService service = Client.getService();
        Call<ArrayList<Receipt>> call = service.getAllReceipt();

        call.enqueue(new Callback<ArrayList<Receipt>>() {
            @Override
            public void onResponse(Call<ArrayList<Receipt>> call, Response<ArrayList<Receipt>> response) {
                if(response.isSuccessful()){
                    ArrayList<Receipt> receiptList = response.body();

                    // Filtrirajte recepte da zadržite samo jedan po datumu
                    List<Receipt> filteredReceipts = filterReceiptsByDate(receiptList);

                    // Ažurirajte RecyclerView
                    documentList.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    adapterDocument = new DocumentListAdapter(filteredReceipts,null, selectedDocument,getContext());
                    documentList.setAdapter(adapterDocument);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Receipt>> call, Throwable throwable) {
                // Obrada greške
            }
        });
    }

    private List<Adjustment> filterAdjustmentsByDate(List<Adjustment> adjustments) {
        Map<String, Adjustment> dateMap = new HashMap<>();

        for (Adjustment adjustment : adjustments) {
            // Pretpostavljam da `getDate()` vraća datum u formatu koji možete koristiti kao ključ
            String date = adjustment.getDatum();
            if (!dateMap.containsKey(date)) {
                dateMap.put(date, adjustment);
            }
        }

        return new ArrayList<>(dateMap.values());
    }

    public void initListAdjustment() {
        UserService service = Client.getService();
        Call<ArrayList<Adjustment>> call = service.getAllAdjustment();

        call.enqueue(new Callback<ArrayList<Adjustment>>() {
            @Override
            public void onResponse(Call<ArrayList<Adjustment>> call, Response<ArrayList<Adjustment>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Adjustment> adjustmentList = response.body();

                    // Filtrirajte prilagođavanja da zadržite samo jedan po datumu
                    List<Adjustment> filteredAdjustments = filterAdjustmentsByDate(adjustmentList);

                    // Ažurirajte RecyclerView
                    documentList.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    adapterDocument = new DocumentListAdapter(null, filteredAdjustments,selectedDocument, getContext());
                    documentList.setAdapter(adapterDocument);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Adjustment>> call, Throwable throwable) {
                // Obrada greške
            }
        });
    }



    private int extractNumberFromSpinner(String text) {
        // Koristimo regularni izraz da izvučemo prvi broj u stringu
        String number = text.replaceAll("\\D+", ""); // Uklanja sve što nije broj
        return Integer.parseInt(number);
    }
}