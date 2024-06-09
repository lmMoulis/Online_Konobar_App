package com.example.onlinekonobar.Activity.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.User;
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

public class Profile extends Fragment {
    TextView name,countOrder,email,date,gender,password;
    ImageView order;
    int orderNum;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name=view.findViewById(R.id.nameSurnameTxt);
        countOrder=view.findViewById(R.id.countOrderTxt);
        email=view.findViewById(R.id.emailProfileTxt);
        date=view.findViewById(R.id.dateProfileTxt);
        gender=view.findViewById(R.id.genderProfileTxt);
        password=view.findViewById(R.id.passwordProfileTxt);
        order=view.findViewById(R.id.getOrders);
        progressBar=view.findViewById(R.id.progressBarProfile);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment invoiceFragment = new InvoiceList();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.profileFragment, invoiceFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        getAllOrder();
        initList();
        return view;
    }
    private void initList() {
        progressBar.setVisibility(View.VISIBLE);
        UserService userService= Client.getService();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int idUser = sharedPreferences.getInt("userId", -1);
        userService.getUserById(idUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        updateUI(user);
                    } else {
                        Toast.makeText(getContext(), "Korisnik nije pronađen", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Greška pri dohvaćanju podataka", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Greška u komunikaciji sa serverom", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void updateUI(User user)
    {
        countOrder.setText(orderNum+"");
        name.setText(user.getIme()+" "+user.getPrezime());
        email.setText(user.getEmail());
        date.setText(convertDateFormat(user.getDatum_Rodenja()));
        gender.setText(user.getSpol());
    }
    public void getAllOrder()
    {
        UserService userService = Client.getService();
        Call<ArrayList<Invoice>>call;
        call=userService.getAllInvoice();
        call.enqueue(new Callback<ArrayList<Invoice>>() {
            @Override
            public void onResponse(Call<ArrayList<Invoice>> call, Response<ArrayList<Invoice>> response) {
                ArrayList<Invoice>list=response.body();
                if (list != null && !list.isEmpty()) {
                    for (Invoice invoice : list) {
                       orderNum++;
                    }
                }

            }
            @Override
            public void onFailure(Call<ArrayList<Invoice>> call, Throwable throwable) {

            }
        });
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