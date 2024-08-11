package com.example.onlinekonobar.Api;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    public static Retrofit getRetrofit() {
        // Kreiranje HTTP logiranja interceprora
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Kreiranje OkHttpClient s dodatkom za logiranje
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Kreiranje Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.20.10.2:5008")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d("DEBUG", "Poziv na adresu: " + retrofit.baseUrl().toString());
return retrofit;
    }

    public static UserService getService() {
        return getRetrofit().create(UserService.class);
    }
}