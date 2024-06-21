package com.example.onlinekonobar.Activity.Waiter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.onlinekonobar.R;

public class SelectTable extends AppCompatActivity {
    Button Stol1,Stol2,Stol3,Stol4,Stol5,Stol6;
    Button profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_table);

        Stol1=findViewById(R.id.stol1);
        Stol2=findViewById(R.id.stol2);
        Stol3=findViewById(R.id.stol3);
        Stol4=findViewById(R.id.stol4);
        Stol5=findViewById(R.id.stol5);
        Stol6=findViewById(R.id.stol6);

        Stol1.setOnClickListener(v -> {
            openNewActivity("Stol-1");
        });
        Stol2.setOnClickListener(v -> {
            openNewActivity("Stol-2");
        });
        Stol3.setOnClickListener(v -> {
            openNewActivity("Stol-3");
        });
        Stol4.setOnClickListener(v -> {
            openNewActivity("Stol-4");
        });
        Stol5.setOnClickListener(v -> {
            openNewActivity("Stol-5");
        });
        Stol6.setOnClickListener(v -> {
            openNewActivity("Stol-6");
        });

        profile=findViewById(R.id.selectTableWaiterBtn);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prvo sakrijte elemente iz aktivnosti
                Stol1.setVisibility(View.GONE);
                Stol2.setVisibility(View.GONE);
                Stol3.setVisibility(View.GONE);
                Stol4.setVisibility(View.GONE);
                Stol5.setVisibility(View.GONE);
                Stol6.setVisibility(View.GONE);
                profile.setVisibility(View.GONE);

                Fragment profileFragment = new com.example.onlinekonobar.Activity.Waiter.Profile();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null) {
                        fragmentTransaction.hide(fragment);
                    }
                }
                fragmentTransaction.replace(R.id.fragmentDetailInvoiceWaiterFalse, profileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                View frameLayout = findViewById(R.id.fragmentDetailInvoiceWaiterFalse);
                frameLayout.setVisibility(View.VISIBLE);
            }
        });



    }
    private void openNewActivity(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("tablePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tableValue", value);
        editor.apply();

        Intent intent = new Intent(SelectTable.this, Articles.class);
        startActivity(intent);
    }
}