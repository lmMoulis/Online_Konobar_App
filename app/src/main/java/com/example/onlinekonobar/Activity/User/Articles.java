package com.example.onlinekonobar.Activity.User;

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

import com.example.onlinekonobar.Activity.User.Adapter.ArticleUserAdapter;
import com.example.onlinekonobar.Adapter.CategoryAdapter;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Articles extends AppCompatActivity implements CategoryAdapter.CategoryClickListener {

    private RecyclerView.Adapter adapterListDrink;
    private RecyclerView.Adapter adapterListCategory;


    private int catId;
    private String searchText;
    private boolean isSearch;

    ProgressBar progressBar;
    RecyclerView article;
    RecyclerView category;
    ImageView searchBtn;
    EditText inputSearch;
    Button home,list,cart,profile;
    int idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        progressBar=findViewById(R.id.userProgressBar);
        article=findViewById(R.id.userArticleRecycler);
        category=findViewById(R.id.userCategoryRecycler);
        searchBtn=findViewById(R.id.userSearchBtn);
        inputSearch=findViewById(R.id.userSearchInp);

        home=findViewById(R.id.getHomeBtn);
        list=findViewById(R.id.getListBtn);
        cart=findViewById(R.id.getCardBtn);
        profile=findViewById(R.id.getProfileBtn);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        idUser=sharedPreferences.getInt("userId",-1);
        getIntentExtra();
        initCategory();

        catId=0;
        initList();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = inputSearch.getText().toString();
                isSearch = !searchText.isEmpty();
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
                fragmentTransaction.replace(R.id.fragmentCard, cardFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                View frameLayout = findViewById(R.id.fragmentCard);
                frameLayout.setVisibility(View.VISIBLE);

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment profileFragment = new Profile();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null) {
                        fragmentTransaction.hide(fragment);
                    }
                }
                fragmentTransaction.replace(R.id.fragmentProfile, profileFragment);
                fragmentTransaction.addToBackStack(null); // Optional: adds this transaction to the back stack
                fragmentTransaction.commit();
                View frameLayout = findViewById(R.id.fragmentProfile);
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

                        //Dohvačanje ID a od odabrane kategorije
                        CategoryAdapter adapterListCategory = new CategoryAdapter(list);
                        adapterListCategory.setCategoryClickListener(Articles.this);
                        category.setAdapter(adapterListCategory);
                    }
                } else {
                    // Handle unsuccessful response
                }
            }
            @Override
            public void onFailure(Call<ArrayList<com.example.onlinekonobar.Api.Category>> call, Throwable t) {
            }
        });
    }
    private void initList() {
        progressBar.setVisibility(View.VISIBLE);

        UserService service = Client.getService();
        Call<ArrayList<Article>> call;
        call = service.getAllArticles();
        call.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(Call<ArrayList<Article>> call, Response<ArrayList<Article>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Article> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        // Filtriranje liste ako je pretraga aktivna
                        if (isSearch) {
                            list = filterArticles(list);
                        } else if (catId !=0) {
                            list=filterArticlesByCategory(list,catId);
                        }
                        article.setLayoutManager(new GridLayoutManager(Articles.this, 2));
                        adapterListDrink = new ArticleUserAdapter(list,Articles.this,idUser);
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
    private void getIntentExtra() {
        catId=getIntent().getIntExtra("CategoryId",0);
        searchText=getIntent().getStringExtra("text");
        isSearch=getIntent().getBooleanExtra("isSearch",false);

    }

}