package com.example.onlinekonobar.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.RegisterRequest;
import com.example.onlinekonobar.Api.RegisterResponse;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    EditText ime,prezime,email,mob,spol,lozinka,ponLozinka;
    Button reg,datum;
    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg=findViewById(R.id.registerBtn);

        ime=findViewById(R.id.imeInp);
        prezime=findViewById(R.id.prezimeInp);
        email=findViewById(R.id.emailInp);
        mob=findViewById(R.id.brojMobitelaInp);
        spol=findViewById(R.id.sInp);
        datum=findViewById(R.id.datumRodenjaInp);
        lozinka=findViewById(R.id.lozinkaInp);
        ponLozinka=findViewById(R.id.ponoviLozinkaInp);

        login=findViewById(R.id.redirectLogin);
        datum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale.setDefault(new Locale("hr", "HR"));
                // Dohvati trenutni datum
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Kreiraj novi DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Postavi odabrani datum na EditText datum
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Formatiraj datum u hrvatski format
                        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy.", new Locale("hr", "HR"));
                        String selectedDateString = sdf.format(selectedDate.getTime());

                        datum.setText(selectedDateString);
                    }
                }, year, month, dayOfMonth);

                // Prikaži DatePickerDialog
                datePickerDialog.show();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kreiranje objekta RegisterRequest s podacima iz EditText polja
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setId(1);
                registerRequest.setIme(ime.getText().toString());
                registerRequest.setPrezime(prezime.getText().toString());
                registerRequest.setEmail(email.getText().toString());
                registerRequest.setBroj_Mobitela(mob.getText().toString());
                registerRequest.setSpol(spol.getText().toString());
                registerRequest.setDatum_Rodenja(datum.getText().toString());
                registerRequest.setLozinka(lozinka.getText().toString());
                registerRequest.setLozinka(ponLozinka.getText().toString());
                Gson gson = new Gson();
                String json = gson.toJson(registerRequest);

                Log.d("DEBUG", "Podaci koji se šalju prema bazi (JSON format): " + json);

                // Poziv metode za registraciju
                UserService userService = Client.getService();
                Call<RegisterResponse> call = userService.registerUsers(registerRequest);
                call.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful()) {
                            // Registracija uspješna
                            RegisterResponse registerResponse = response.body();
                            Log.d("DEBUG", "Odgovor od servera: " + registerResponse.toString());
                            Toast.makeText(Register.this, "Registracija uspješna", Toast.LENGTH_SHORT).show();
                            // Možete dodati dodatne akcije kao što su prijava korisnika ili navigacija na drugi zaslon
                        } else {
                            // Greška prilikom registracije
                            Log.e("ERROR", "Greška prilikom registracije: " + response.code());
                            Toast.makeText(Register.this, "Greška prilikom registracije", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        // Greška prilikom komunikacije sa serverom
                        Log.e("ERROR", "Greška prilikom komunikacije sa serverom: " + t.getMessage());
                        Toast.makeText(Register.this, "Greška prilikom komunikacije sa serverom", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
        datum.setText(selectedDate);
    }*/
}