package com.example.onlinekonobar.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.RegisterRequest;
import com.example.onlinekonobar.Api.RegisterResponse;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    EditText ime,prezime,email,mob,spol,datum,lozinka,ponLozinka;
    Button reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg=findViewById(R.id.regBtn);

        ime=findViewById(R.id.imeInp);
        prezime=findViewById(R.id.prezimeInp);
        email=findViewById(R.id.emailInp);
        mob=findViewById(R.id.brojMobitelaInp);
        spol=findViewById(R.id.sInp);
        datum=findViewById(R.id.datumRodenjaInp);
        lozinka=findViewById(R.id.lozinkaInp);
        ponLozinka=findViewById(R.id.ponoviLozinkaInp);



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
// Pre
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
}