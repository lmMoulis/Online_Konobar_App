package com.example.onlinekonobar.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @POST("api/Korisnik/login")
    Call<LoginResponse> loginUsers(@Body LoginRequest loginRequest);
    @POST("api/Korisnik")
    Call<RegisterResponse> registerUsers(@Body RegisterRequest registerRequest);
    @POST("api/StavkaControllers")
    Call<Void> saveCard(@Body Item item);
    @POST("api/RacunControllers")
    Call<Void> saveInvoice(@Body Invoice invoice);
    @PUT("api/SkladisteControllers/{id}")
    Call<Void> updateStock(@Path("id") int id, @Body Stock stock);


    @GET("api/ArtikalControllers")
    Call<ArrayList<Article>> getAllArticles();
    @GET("api/ArtikalControllers/{id}")
    Call<Article> getArticleById(@Path("id") int articleId);

    @GET("api/ArtikalControllers")
    Call<ArrayList<Article>> searchArticles(@Query("searchText") String searchText);

    @GET("api/ArtikalControllers")
    Call<ArrayList<Article>> getArticlesByCategory(@Query("kategorija_id") int categoryId);

    @GET("api/KategorijaControllers")
    Call<ArrayList<Category>> getAllCategory();
    @GET("api/PrilagodbaControllers")
    Call<ArrayList<Customize>> getAllCustomize();

    @GET("/api/SkladisteControllers/{articleId}")
    Call<Stock> getStockByArticleId(@Path("articleId") int articleId);
    @GET("api/RacunControllers/{invoiceId}")
    Call<Invoice>getInvoiceById(@Path("invoiceId") int invoiceId);
    @GET("api/RacunControllers/{userId}")
    Call<ArrayList<Invoice>>getInvoiceByUserId(@Path("userId") int userId);
    @GET("api/Korisnik/{id}")
    Call<User> getUserById(@Path("id") int id);
    @GET("api/RacunControllers")
    Call<ArrayList<Invoice>>getAllInvoice();
    @GET("api/SkladisteControllers")
    Call<ArrayList<Stock>>getAllStock();
    @GET("api/StavkaControllers")
    Call<ArrayList<Item>>getAllItems();

    @PUT("api/RacunControllers/{id}")
    Call<Void> putNewElementInvoice(@Path("id") int id, @Body Invoice invoice);
    @PUT("api/Korisnik/{id}")
    Call<Void> setUserImage(@Path("id")int id ,@Body User user);

    @GET("api/Korisnik")
    Call<ArrayList<User>>getAllUsers();

    @GET("api/NormativiControllers/article/{articleId}")
    Call<ArrayList<Normative>>getNormativeByArticleId(@Path("articleId") int articleId);
    @GET("api/SkladisteControllers/{id}")
    Call<Stock> getStockById(@Path("id") int id);

    @GET("api/ArtikalControllers/days-remaining")
    Call<ArrayList<Remaining>>getRemainingDaysAll();

    @GET("api/PrimkaControllers")
    Call<ArrayList<Receipt>>getAllReceipt();
    @POST("api/PrimkaControllers")
    Call<Receipt> createReceipt(@Body Receipt receipt);
    @PUT("api/PrimkaControllers/{id}")
    Call<Void> updateReceipt(@Path("id") int id, @Body Receipt receipt);
    // Promenite metodu ako API vraća listu objekata
    @GET("api/PrimkaControllers/date")
    Call<List<Receipt>> getReceiptsByStockIdAndDate(@Query("stockId") int stockId, @Query("date") String date);

    @GET("api/OtpisControllers")
    Call<ArrayList<Adjustment>>getAllAdjustment();
    @POST("api/OtpisControllers")
    Call<Adjustment> createAdjustment(@Body Adjustment adjustment);
    @PUT("api/OtpisControllers/{id}")
    Call<Void> updateAdjustment(@Path("id") int id, @Body Adjustment adjustment);
    // Promenite metodu ako API vraća listu objekata
    @GET("api/OtpisControllers/date")
    Call<List<Adjustment>> getAdjustmentByStockIdAndDate(@Query("stockId") int stockId, @Query("date") String date);


    @GET("api/SkladisteControllers/{id}")
    Call<Stock> getStockDetails(@Path("id") int stockId);
    @GET("api/PrimkaControllers/bydate") Call<ArrayList<Receipt>> getReceiptsByDate(@Query("date") String date);
    @GET("api/OtpisControllers/bydate") Call<ArrayList<Adjustment>> getAdjustmentByDate(@Query("date") String date);
}

