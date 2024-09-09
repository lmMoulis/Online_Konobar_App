package com.example.onlinekonobar.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinekonobar.Activity.Admin.Stock;
import com.example.onlinekonobar.Activity.User.ScanQR;
import com.example.onlinekonobar.Activity.Waiter.Articles;
import com.example.onlinekonobar.Activity.Waiter.SelectTable;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.LoginRequest;
import com.example.onlinekonobar.Api.LoginResponse;
import com.example.onlinekonobar.Api.RegisterResponse;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText email,password;
    Button login;
    TextView redirectReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.emailLoginInp);
        password=findViewById(R.id.passwordLoginInp);
        login=findViewById(R.id.loginBtn);
        redirectReg=findViewById(R.id.redirectRegister);


        redirectReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginRequest loginRequest= new LoginRequest();

                loginRequest.setEmail(email.getText().toString());
                loginRequest.setPassword(password.getText().toString());

                Gson gson= new Gson();
                String json= gson.toJson(loginRequest);
                Log.d("DEBUG", "Podaci koji se šalju prema bazi (JSON format): " + json);

                UserService userService = Client.getService();
                Call<LoginResponse> call = userService.loginUsers(loginRequest);

                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            LoginResponse loginResponse = response.body();
                            int pristup=loginResponse.getPristup();
                            Log.d("Debug","Pristup"+pristup);



                            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            int userId = loginResponse.getId();
                            editor.putInt("userId", userId);
                            editor.putBoolean("isLoggedIn", true);
                            editor.putInt("userAccessLevel", pristup);
                            editor.apply();


                            Intent intent;
                            switch (pristup) {
                                case 1:
                                    intent = new Intent(Login.this, ScanQR.class);
                                    break;
                                case 2:
                                    intent = new Intent(Login.this, SelectTable.class);
                                    break;
                                case 3:
                                    intent = new Intent(Login.this, Stock.class);
                                    break;
                                default:
                                    Toast.makeText(Login.this, "Nepoznat pristup!", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(Login.this, Login.class);
                                    break;
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Greška prilikom prijave", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(Login.this, "Greška prilikom komunikacije sa serverom", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}