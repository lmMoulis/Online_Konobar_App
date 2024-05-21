package com.example.onlinekonobar.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                Intent intent = new Intent(HomeScreen.this, Articles.class);
                startActivity(intent);
            }
        });
    }
}