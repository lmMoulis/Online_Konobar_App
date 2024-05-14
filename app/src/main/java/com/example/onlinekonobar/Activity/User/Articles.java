package com.example.onlinekonobar.Activity.User;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Adapter.ArticleUserAdapter;
import com.example.onlinekonobar.Adapter.CategoryAdapter;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Articles extends AppCompatActivity {

    private RecyclerView.Adapter adapterListDrink;
    private RecyclerView.Adapter adapterListCategory;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;

    ProgressBar progressBar;
    RecyclerView article;
    RecyclerView category;
    ImageView searchBtn;
    EditText inputSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        progressBar=findViewById(R.id.userProgressBar);
        article=findViewById(R.id.userArticleRecycler);
        category=findViewById(R.id.userCategoryRecycler);
        searchBtn=findViewById(R.id.userSearchBtn);
        inputSearch=findViewById(R.id.userSearchInp);
        getIntentExtra();
        initCategory();
        initList();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = inputSearch.getText().toString();
                isSearch = !searchText.isEmpty();
                initList();
            }
        });
    }

    public void initCategory()
    {
        UserService service = Client.getService();
        Call<ArrayList<com.example.onlinekonobar.Api.Category>> call;
        call = service.getAllCategory();

        call.enqueue(new Callback<ArrayList<com.example.onlinekonobar.Api.Category>>() {
            @Override
            public void onResponse(Call<ArrayList<com.example.onlinekonobar.Api.Category>> call, Response<ArrayList<com.example.onlinekonobar.Api.Category>> response) {
                if (response.isSuccessful()) {
                    ArrayList<com.example.onlinekonobar.Api.Category> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        for (com.example.onlinekonobar.Api.Category category : list) {
                            Log.d("Category", "Title: " + category.getNaziv());

                        }

                        category.setLayoutManager(new LinearLayoutManager(Articles.this, LinearLayoutManager.HORIZONTAL,false));


                        adapterListCategory = new CategoryAdapter(list);
                        category.setAdapter(adapterListCategory);
                    }
                } else {
                    // Handle unsuccessful response
                }
                // progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<com.example.onlinekonobar.Api.Category>> call, Throwable t) {
                // Handle failure
//                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void initList() {
        progressBar.setVisibility(View.VISIBLE);

        UserService service = Client.getService();
        Call<ArrayList<Article>> call;

        if (isSearch) {
            call = service.searchArticles(searchText);
        } else {
            call = service.getAllArticles();
        }

        call.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(Call<ArrayList<Article>> call, Response<ArrayList<Article>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Article> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        // Filtriranje liste ako je pretraga aktivna
                        if (isSearch) {
                            list = filterArticles(list);
                        }

                        article.setLayoutManager(new GridLayoutManager(Articles.this, 2));
                        adapterListDrink = new ArticleUserAdapter(list);
                        article.setAdapter(adapterListDrink);
                    }
                } else {
                    // Handle unsuccessful response
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<Article>> call, Throwable t) {
                // Handle failure
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Metoda za filtriranje liste članaka na temelju unesenog teksta
    private ArrayList<Article> filterArticles(ArrayList<Article> articles) {
        ArrayList<Article> filteredList = new ArrayList<>();
        for (Article article : articles) {
            if (article.getNaziv().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(article);
            }
        }
        return filteredList;
    }

    private void getIntentExtra() {
        categoryId=getIntent().getIntExtra("CategoryId",1);
        categoryName=getIntent().getStringExtra("Category");
        searchText=getIntent().getStringExtra("text");
        isSearch=getIntent().getBooleanExtra("isSearch",false);

    }

}