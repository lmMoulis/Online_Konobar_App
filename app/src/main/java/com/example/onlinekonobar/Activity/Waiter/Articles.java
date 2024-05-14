package com.example.onlinekonobar.Activity.Waiter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Adapter.ArticleUserAdapter;
import com.example.onlinekonobar.Adapter.ArticleWaiterAdapter;
import com.example.onlinekonobar.Adapter.CategoryAdapter;
import com.example.onlinekonobar.Api.Category;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Articles extends AppCompatActivity {
    private RecyclerView.Adapter adapterListWaiterDrink;
    private RecyclerView.Adapter adapterListCategory;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private Boolean isSearch;
    ProgressBar progressBar;
    RecyclerView article;
    RecyclerView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_waiter);

        progressBar=findViewById(R.id.waiterProgressBar);
        article=findViewById(R.id.waiterArticleRecycler);
        category=findViewById(R.id.waiterCategoryRecycler);

        getIntentExtra();
        initCategory();
        initList();
    }
    public void initCategory()
    {
        UserService service = Client.getService();
        Call<ArrayList<Category>> call;
        call = service.getAllCategory();

        call.enqueue(new Callback<ArrayList<com.example.onlinekonobar.Api.Category>>() {
            @Override
            public void onResponse(retrofit2.Call<ArrayList<Category>> call, Response<ArrayList<Category>> response) {
                if (response.isSuccessful()) {
                    ArrayList<com.example.onlinekonobar.Api.Category> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        for (com.example.onlinekonobar.Api.Category category : list) {
                            Log.d("Category", "Title: " + category.getNaziv());

                        }

                        category.setLayoutManager(new LinearLayoutManager(com.example.onlinekonobar.Activity.Waiter.Articles.this, LinearLayoutManager.HORIZONTAL,false));

                        adapterListCategory = new CategoryAdapter(list);
                        category.setAdapter(adapterListCategory);
                    }
                } else {
                    // Handle unsuccessful response
                }
                // progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(retrofit2.Call<ArrayList<Category>> call, Throwable t) {
                // Handle failure
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    public void initList()
    {
        progressBar.setVisibility(View.VISIBLE);
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
                        }

                        article.setLayoutManager(new GridLayoutManager(com.example.onlinekonobar.Activity.Waiter.Articles.this, 3));
                        adapterListWaiterDrink = new ArticleWaiterAdapter(list);
                        article.setAdapter(adapterListWaiterDrink);
                    }
                } else {
                    // Handle unsuccessful response
                }
                 progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<com.example.onlinekonobar.Api.Article>> call, Throwable t) {
                // Handle failure
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void getIntentExtra()
    {
        categoryId=getIntent().getIntExtra("CategoryId",1);
        categoryName=getIntent().getStringExtra("Category");
        searchText=getIntent().getStringExtra("text");
        isSearch=getIntent().getBooleanExtra("isSearch",false);
    }
}