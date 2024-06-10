package com.example.onlinekonobar.Activity.Waiter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Activity.Waiter.Adapter.ArticleWaiterAdapter;
import com.example.onlinekonobar.Adapter.CategoryAdapter;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Category;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Articles extends AppCompatActivity implements CategoryAdapter.CategoryClickListener {
    private RecyclerView.Adapter adapterListWaiterDrink;
    private RecyclerView.Adapter adapterListCategory;
    private int catId;
    private String searchText;
    private Boolean isSearch;
    ProgressBar progressBar;
    RecyclerView article;
    RecyclerView category;
    ImageView searchBtn;
    EditText inputSearch;
    Button cart;
    int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_waiter);

        progressBar=findViewById(R.id.waiterProgressBar);
        article=findViewById(R.id.waiterArticleRecycler);
        category=findViewById(R.id.waiterCategoryRecycler);
        searchBtn=findViewById(R.id.waiterSearchBtn);
        inputSearch=findViewById(R.id.waiterSearchInp);
        cart=findViewById(R.id.getCardWaiterBtn);
        getIntentExtra();
        initCategory();
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        idUser=sharedPreferences.getInt("userId",-1);
        getIntentExtra();
        initCategory();
        catId=0;
        initList();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText=inputSearch.getText().toString();
                isSearch =!searchText.isEmpty();
                initList();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment cardFragment = new Card();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null) {
                        fragmentTransaction.hide(fragment);
                    }
                }
                fragmentTransaction.replace(R.id.fragmentCartWaiter, cardFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                View frameLayout = findViewById(R.id.fragmentCartWaiter);
                frameLayout.setVisibility(View.VISIBLE);

            }
        });
    }
    @Override
    public void onCategoryClicked(int categoryId) {
        // Ovdje primite CategoryId i izvršite željene radnje
        Log.d("Category", "Kategorija ID: " + categoryId);
        if(categoryId==catId) {
            catId=0;
        }
        else {
            catId=categoryId;}
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

                        CategoryAdapter adapterListCategory = new CategoryAdapter(list);
                        adapterListCategory.setCategoryClickListener(Articles.this);
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
        call = service.getAllArticles();

        call.enqueue(new Callback<ArrayList<com.example.onlinekonobar.Api.Article>>() {
            @Override
            public void onResponse(Call<ArrayList<com.example.onlinekonobar.Api.Article>> call, Response<ArrayList<com.example.onlinekonobar.Api.Article>> response) {
                if (response.isSuccessful()) {
                    ArrayList<com.example.onlinekonobar.Api.Article> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        if(isSearch){
                            list=filterArticles(list);
                        } else if (catId !=0) {
                            list=filterArticlesByCategory(list,catId);
                        }

                        article.setLayoutManager(new GridLayoutManager(com.example.onlinekonobar.Activity.Waiter.Articles.this, 3));
                        adapterListWaiterDrink = new ArticleWaiterAdapter(list,Articles.this,idUser);
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
    private ArrayList<Article> filterArticles(ArrayList<Article> articles) {
        ArrayList<Article> filteredList = new ArrayList<>();
        for (Article article : articles) {
            if (article.getNaziv().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(article);
            }
        }
        return filteredList;
    }
    private ArrayList<Article>filterArticlesByCategory(ArrayList<Article>articles,int categoryId) {
        ArrayList<Article> filteredList = new ArrayList<>();
        for (Article article : articles) {
            if (article.getKategorija_Id() == categoryId) {
                filteredList.add(article);
            }
        }
        return filteredList;
    }

    private void getIntentExtra()
    {
        catId=getIntent().getIntExtra("CategoryId",1);
        searchText=getIntent().getStringExtra("text");
        isSearch=getIntent().getBooleanExtra("isSearch",false);
    }
}