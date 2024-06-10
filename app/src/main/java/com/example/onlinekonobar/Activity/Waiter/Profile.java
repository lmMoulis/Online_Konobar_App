package com.example.onlinekonobar.Activity.Waiter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.onlinekonobar.R;

public class Profile extends Fragment {
    TextView name,countOrder,email,date,gender,password,logout;
    ImageView order;
    int orderNum;
    ProgressBar progressBar;

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
        return view;
    }
}