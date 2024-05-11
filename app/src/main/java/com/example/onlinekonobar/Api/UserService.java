package com.example.onlinekonobar.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @POST("api/Korisnik/login")
    Call<LoginResponse> loginUsers(@Body LoginRequest loginRequest);
    @POST("api/Korisnik")
    Call<RegisterResponse> registerUsers(@Body RegisterRequest registerRequest);
    @GET("api/ArtikalControllers")
    Call<ArrayList<Article>> getAllArticles();

    @GET("api/ArtikalControllers")
    Call<ArrayList<Article>> searchArticles(@Query("searchText") String searchText);

    @GET("api/ArtikalControllers")
    Call<ArrayList<Article>> getArticlesByCategory(@Query("kategorija_id") int categoryId);
}

