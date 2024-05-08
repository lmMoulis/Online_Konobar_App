package com.example.onlinekonobar.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("api/Korisnik")
    Call<RegisterResponse> registerUsers(@Body RegisterRequest registerRequest);
}
