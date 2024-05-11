package com.example.onlinekonobar.Activity.User;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Adapter.ArticleAdapter;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Articles extends AppCompatActivity {

    private RecyclerView.Adapter adapterListDrink;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;

    ProgressBar progressBar;
    RecyclerView article;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        progressBar=findViewById(R.id.userProgressBar);
        article=findViewById(R.id.userArticleRecycler);

        getIntentExtra();
        initList();
    }


    private void initList() {
        // progressBar.setVisibility(View.VISIBLE);

        UserService service = Client.getService();
        Call<ArrayList<com.example.onlinekonobar.Api.Article>> call;

        if (isSearch) {
            call = service.searchArticles(searchText);
        } else {
            call = service.getAllArticles();
        }

        call.enqueue(new Callback<ArrayList<com.example.onlinekonobar.Api.Article>>() {
            @Override
            public void onResponse(Call<ArrayList<com.example.onlinekonobar.Api.Article>> call, Response<ArrayList<com.example.onlinekonobar.Api.Article>> response) {
                if (response.isSuccessful()) {
                    ArrayList<com.example.onlinekonobar.Api.Article> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        // Ispisivanje podataka u konzolu
                        for (com.example.onlinekonobar.Api.Article article : list) {
                            Log.d("Article", "Naziv: " + article.getNaziv() + ", Cijena: " + article.getCijena()+ ", SLika: " + article.getSlika());
                        }

                        article.setLayoutManager(new GridLayoutManager(Articles.this, 2));
                        adapterListDrink = new ArticleAdapter(list);
                        article.setAdapter(adapterListDrink);
                    }
                } else {
                    // Handle unsuccessful response
                }
                // progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<com.example.onlinekonobar.Api.Article>> call, Throwable t) {
                // Handle failure
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void getIntentExtra() {
        categoryId=getIntent().getIntExtra("CategoryId",1);
        categoryName=getIntent().getStringExtra("Category");
        searchText=getIntent().getStringExtra("text");
        isSearch=getIntent().getBooleanExtra("isSearch",false);

    }

}