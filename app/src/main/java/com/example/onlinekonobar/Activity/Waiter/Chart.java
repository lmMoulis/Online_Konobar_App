package com.example.onlinekonobar.Activity.Waiter;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinekonobar.Activity.Waiter.Adapter.ArticleSpinnerAdapter;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
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

public class Chart extends Fragment {

    BarChart chart;
    LinearLayout selectDate, selectDring;
    TextView datum;
    String selectedDateString;
    SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd. MMMM yyyy.", new Locale("hr", "HR"));
    SimpleDateFormat comparisonDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Spinner spinner, spinnerSelectChart,spinnerItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_waiter, container, false);

        chart = view.findViewById(R.id.barChart);
        selectDate = view.findViewById(R.id.selectDateChartWaiter);
        selectDring= view.findViewById(R.id.linearSelectDrink);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.spinner_values,
                R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinnerSelectChart=view.findViewById(R.id.spinnerSelect);
        ArrayAdapter<CharSequence> adapterChart = ArrayAdapter.createFromResource(
                getContext(),
                R.array.chart_value,
                R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectChart.setAdapter(adapterChart);
        spinnerSelectChart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (selectedItem.equals("Statistika pojedinog pića")) {
                    selectDring.setVisibility(View.VISIBLE);
                } else {
                    selectDring.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        spinnerItems=view.findViewById(R.id.spinnerDrink);

        fetchArticles();


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

                        // Dobijte broj dana iz spinner-a koristeći novu metodu
                        int numberOfDays = extractNumberFromSpinner(spinner.getSelectedItem().toString());
                        getChartData(selectedDate, numberOfDays);
                    }
                }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });



        return view;
    }
    private void fetchArticles() {
        UserService service = Client.getService();
        Call<ArrayList<Article>> call = service.getAllArticles();
        call.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(Call<ArrayList<Article>> call, Response<ArrayList<Article>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body();

                    // Postavljanje adaptera za spinnerItems
                    ArticleSpinnerAdapter adapterItems = new ArticleSpinnerAdapter(getContext(), articles);
                    spinnerItems.setAdapter(adapterItems);


                }
            }

            @Override
            public void onFailure(Call<ArrayList<Article>> call, Throwable throwable) {
                Toast.makeText(getContext(), "Greška u dohvaćanju artikala", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private int extractNumberFromSpinner(String text) {
        // Koristimo regularni izraz da izvučemo prvi broj u stringu
        String number = text.replaceAll("\\D+", ""); // Uklanja sve što nije broj
        return Integer.parseInt(number);
    }

    public void getChartData(Calendar selectedDate, int numberOfDays) {
        UserService userService = Client.getService();

        Calendar startDate = (Calendar) selectedDate.clone();
        startDate.add(Calendar.DAY_OF_YEAR, -numberOfDays);

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
                                                        String selectedChartType = spinnerSelectChart.getSelectedItem().toString();
                                                        if (selectedChartType.equals("Statistika pojedinog pića")) {
                                                            Article selectedArticle = (Article) spinnerItems.getSelectedItem();
                                                            Map<String, Integer> dailySales = processDailySalesData(allInvoices, allItems, selectedArticle, startDate, selectedDate);
                                                            displayChart(dailySales);
                                                        } else {
                                                            Map<String, Integer> consumptionData = processConsumptionData(allArticles, allInvoices, allItems, startDate, selectedDate);
                                                            displayChart(consumptionData);
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ArrayList<Item>> call, Throwable throwable) {
                                                Toast.makeText(getContext(), "Greška u dohvaćanju stavki", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Invoice>> call, Throwable throwable) {
                                Toast.makeText(getContext(), "Greška u dohvaćanju računa", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Article>> call, Throwable throwable) {
                Toast.makeText(getContext(), "Greška u dohvaćanju artikala", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Map<String, Integer> processDailySalesData(ArrayList<Invoice> allInvoices, ArrayList<Item> allItems, Article selectedArticle, Calendar startDate, Calendar endDate) {
        Map<String, Integer> dailySales = new HashMap<>();

        Calendar currentDate = (Calendar) startDate.clone();
        while (!currentDate.after(endDate)) {
            String dateString = comparisonDateFormat.format(currentDate.getTime());
            dailySales.put(formatDateForDisplay(dateString), 0);
            currentDate.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (Invoice invoice : allInvoices) {
            try {
                Date invoiceDate = apiDateFormat.parse(invoice.getDatum());
                String invoiceDateString = comparisonDateFormat.format(invoiceDate);

                if (invoiceDateString.compareTo(comparisonDateFormat.format(startDate.getTime())) >= 0 &&
                        invoiceDateString.compareTo(comparisonDateFormat.format(endDate.getTime())) <= 0) {

                    String formattedDate = formatDateForDisplay(invoiceDateString);

                    for (Item item : allItems) {
                        if (item.getArtikal_Id() == selectedArticle.getId() && item.getOrder_Id().equals(invoice.getBroj_Racuna())) {
                            dailySales.put(formattedDate, dailySales.get(formattedDate) + item.getKolicina());
                        }
                    }
                }
            } catch (ParseException e) {
                Log.e("Chart", "Date parsing error", e);
            }
        }

        return dailySales;
    }

    private String formatDateForDisplay(String dateString) {
        try {
            Date date = comparisonDateFormat.parse(dateString);
            return displayDateFormat.format(date);
        } catch (ParseException e) {
            Log.e("Chart", "Date formatting error", e);
            return dateString; // Vrati originalni string ako se desi greška
        }
    }


    private Map<String, Integer> processConsumptionData(ArrayList<Article> allArticles, ArrayList<Invoice> allInvoices, ArrayList<Item> allItems, Calendar startDate, Calendar selectedDate) {
        Map<String, Integer> consumptionData = new HashMap<>();

        // Formatirajte početni i krajnji datum
        String startDateString = comparisonDateFormat.format(startDate.getTime());
        String selectedDateString = comparisonDateFormat.format(selectedDate.getTime());

        for (Invoice invoice : allInvoices) {
            try {
                String invoiceDateString = invoice.getDatum();
                Date invoiceDate = apiDateFormat.parse(invoiceDateString);

                // Formatirajte datum računa da uklonite vremenski deo
                String invoiceDateOnly = comparisonDateFormat.format(invoiceDate);

                // Proverite da li je datum računa unutar vremenskog opsega
                if (invoiceDateOnly.compareTo(startDateString) >= 0 && invoiceDateOnly.compareTo(selectedDateString) <= 0) {
                    for (Item item : allItems) {
                        if (item.getOrder_Id().equals(invoice.getBroj_Racuna())) {
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

            // Sakrivanje količine iznad stupaca ako je količina 0
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getBarLabel(BarEntry barEntry) {
                    if (barEntry.getY() == 0) {
                        return ""; // Vraća prazan string ako je količina 0
                    } else {
                        return String.valueOf((int) barEntry.getY());
                    }
                }
            });

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

            // Postavite boju teksta za X osu
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setTextColor(Color.WHITE); // Postavljanje boje teksta za X osu
            xAxis.setLabelRotationAngle(-90f);
            chart.setPadding(30, 200, 30, 30); // Povećajte gornji padding
            chart.getLayoutParams().height = 1200; // Povećajte visinu grafika



            // Postavite boju teksta za Y osu (levu)
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTextColor(Color.WHITE); // Postavljanje boje teksta za levu Y osu

            // Postavite boju teksta za Y osu (desnu)
            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setTextColor(Color.WHITE); // Postavljanje boje teksta za desnu Y osu

            chart.setFitBars(true);
            chart.invalidate(); // Osvežavanje grafika
        }
    }

}
