package com.example.onlinekonobar.Activity.Waiter;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chart extends Fragment {

    BarChart chart;
    LinearLayout selectDate;
    TextView datum;
    String selectedDateString;
    SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
    SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd. MMMM yyyy.", new Locale("hr", "HR"));
    SimpleDateFormat comparisonDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_waiter, container, false);

        chart = view.findViewById(R.id.barChart);
        selectDate = view.findViewById(R.id.selectDateChartWaiter);
        datum = view.findViewById(R.id.dateChart);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale.setDefault(new Locale("hr", "HR"));
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        selectedDateString = comparisonDateFormat.format(selectedDate.getTime());
                        datum.setText(displayDateFormat.format(selectedDate.getTime()));

                        getChartData(selectedDate);
                    }
                }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });
        return view;
    }

    public void getChartData(Calendar selectedDate) {
        UserService userService = Client.getService();
        userService.getAllArticles().enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(Call<ArrayList<Article>> call, Response<ArrayList<Article>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Article> allArticles = response.body();
                    if (allArticles != null && !allArticles.isEmpty()) {
                        userService.getAllInvoice().enqueue(new Callback<ArrayList<Invoice>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Invoice>> call, Response<ArrayList<Invoice>> response) {
                                if (response.isSuccessful()) {
                                    ArrayList<Invoice> allInvoices = response.body();
                                    if (allInvoices != null && !allInvoices.isEmpty()) {
                                        userService.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
                                            @Override
                                            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                                                if (response.isSuccessful()) {
                                                    ArrayList<Item> allItems = response.body();
                                                    if (allItems != null && !allItems.isEmpty()) {
                                                        Map<String, Integer> consumptionData = processConsumptionData(allArticles, allInvoices, allItems, selectedDate);
                                                        displayChart(consumptionData);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ArrayList<Item>> call, Throwable throwable) {
                                                Toast.makeText(getContext(), "Greska u dohvačanju stavki", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Invoice>> call, Throwable throwable) {
                                Toast.makeText(getContext(), "Greška u dohvačanju računa", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Article>> call, Throwable throwable) {
                Toast.makeText(getContext(), "Greška u dohvačanju artikala", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map<String, Integer> processConsumptionData(ArrayList<Article> allArticles, ArrayList<Invoice> allInvoices, ArrayList<Item> allItems, Calendar selectedDate) {
        Map<String, Integer> consumptionData = new HashMap<>();

        String selectedDateString = comparisonDateFormat.format(selectedDate.getTime());

        for (Invoice invoice : allInvoices) {
            try {
                String invoiceDateString = invoice.getDatum();
                String invoiceDate = comparisonDateFormat.format(apiDateFormat.parse(invoiceDateString));
                if (invoiceDate.equals(selectedDateString)) {
                    for (Item item : allItems) {
                        if (item.getId() == invoice.getId()) {
                            int quantity = item.getKolicina();

                            Article article = findArticleById(allArticles, item.getArtikal_Id());
                            if (article != null) {
                                consumptionData.put(article.getNaziv(), consumptionData.getOrDefault(article.getNaziv(), 0) + quantity);
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                Log.e("Chart", "Date parsing error", e);
            }
        }
        return consumptionData;
    }

    private Article findArticleById(ArrayList<Article> allArticles, int artikalId) {
        for (Article article : allArticles) {
            if (article.getId() == artikalId) {
                return article;
            }
        }
        return null;
    }

    private void displayChart(Map<String, Integer> consumptionData) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : consumptionData.entrySet()) {
            String articleName = entry.getKey();
            int quantity = entry.getValue();

            entries.add(new BarEntry(index, quantity));
            labels.add(articleName);
            index++;
        }

        if (entries.isEmpty()) {
            Toast.makeText(getContext(), "Nema dostupnih podataka za odabrani datum", Toast.LENGTH_SHORT).show();
        } else {
            BarDataSet dataSet = new BarDataSet(entries, "Potrošnja po artiklima");

            // Postavite tekst iznad barova
            dataSet.setValueTextSize(15f); // Veličina teksta
            dataSet.setValueTextColor(Color.WHITE); // Boja teksta

            // Postavite boje barova
            List<Integer> colors = new ArrayList<>();
            for (int i = 0; i < entries.size(); i++) {
                colors.add(Color.rgb((i * 30) % 255, (i * 60) % 255, (i * 90) % 255)); // Primer za različite boje
            }
            dataSet.setColors(colors);

            BarData barData = new BarData(dataSet);
            chart.setData(barData);

            chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            chart.getXAxis().setGranularity(1f);
            chart.getXAxis().setGranularityEnabled(true);

            Description description = new Description();
            description.setText("Potrošnja po artiklima"); // Postavljanje novog opisa grafika
            chart.setDescription(description);

            chart.setFitBars(true);
            chart.invalidate(); // Osvežavanje grafika
        }
    }
}
