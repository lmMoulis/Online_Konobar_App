package com.example.onlinekonobar.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.onlinekonobar.Activity.User.Articles;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.R;

public class HomeScreen extends AppCompatActivity {
    private Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        startButton = findViewById(R.id.loginBtn);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Provjera je li korisnik već prijavljen
                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                int userAccessLevel = sharedPreferences.getInt("userAccessLevel", -1);

                Intent intent;
                if (isLoggedIn) {
                    // Ako je korisnik prijavljen, preusmjeri ga na odgovarajuću aktivnost
                    switch (userAccessLevel) {
                        case 1:
                            intent = new Intent(HomeScreen.this, com.example.onlinekonobar.Activity.User.Articles.class);
                            break;
                        case 2:
                            intent = new Intent(HomeScreen.this, Articles.class);
                            break;
                        default:
                            Toast.makeText(HomeScreen.this, "Nepoznat pristup!", Toast.LENGTH_SHORT).show();
                            intent = new Intent(HomeScreen.this, Login.class);
                            break;
                    }
                } else {
                    // Ako korisnik nije prijavljen, preusmjeri ga na Login aktivnost
                    intent = new Intent(HomeScreen.this, Login.class);
                }
                startActivity(intent);
            }
        });
    }
}