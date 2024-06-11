package com.example.onlinekonobar.Activity.Waiter;

import android.content.Context;
import android.content.Intent;
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

import com.example.onlinekonobar.Activity.Login;
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
    TextView name,countOrder,email,date,gender,password,logout;
    ImageView order;
    int orderNum;
    ProgressBar progressBar;
    int idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile_waiter,container,false);
        name=view.findViewById(R.id.nameSurnameWaiterTxt);
        countOrder=view.findViewById(R.id.countOrderWaiterTxt);
        email=view.findViewById(R.id.emailProfileWaiterTxt);
        date=view.findViewById(R.id.dateProfileWaiterTxt);
        gender=view.findViewById(R.id.genderProfileWaiterTxt);
        password=view.findViewById(R.id.passwordProfileWaiterTxt);
        order=view.findViewById(R.id.getWaiterOrders);
        progressBar=view.findViewById(R.id.progressBarWaiterProfile);
        logout=view.findViewById(R.id.logoutWaiterBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to Login activity
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                getActivity().finish(); // Close current activity

            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment invoiceFragment = new InvoiceList();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.profileFragmentWaiter, invoiceFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        initList();
        getAllOrder();
        return view;
    }
    private void initList() {
        progressBar.setVisibility(View.VISIBLE);
        UserService userService= Client.getService();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt("userId", -1);
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