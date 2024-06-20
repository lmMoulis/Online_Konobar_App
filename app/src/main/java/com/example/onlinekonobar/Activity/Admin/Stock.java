package com.example.onlinekonobar.Activity.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Activity.Admin.Adapter.StockAdminAdapter;
import com.example.onlinekonobar.Activity.User.Adapter.ArticleUserAdapter;
import com.example.onlinekonobar.Activity.User.Articles;
import com.example.onlinekonobar.Activity.Waiter.TakeOrder;
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

public class Stock extends AppCompatActivity implements CategoryAdapter.CategoryClickListener {
    private RecyclerView.Adapter adapterListAdminDrink;
    private RecyclerView.Adapter adapterListCategory;
    private int catId;
    private String searchText;
    private Boolean isSearch;
    ProgressBar progressBar;
    RecyclerView article;
    RecyclerView category;
    ImageView searchBtn;
    EditText inputSearch;
    ImageButton home,newWaiter,allOrder,profile;
    int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        progressBar=findViewById(R.id.adminProgressBar);
        article=findViewById(R.id.adminArticleRecycler);
        category=findViewById(R.id.adminCategoryRecycler);
        searchBtn=findViewById(R.id.adminSearchBtn);
        inputSearch=findViewById(R.id.adminSearchInp);
        home=findViewById(R.id.getHomeAdminBtn);
        newWaiter=findViewById(R.id.getNewWaiterBtn);
        allOrder=findViewById(R.id.getAllOrdersBtn);
        profile=findViewById(R.id.getProfileAdminBtn);

        getIntentExtra();
        catId=0;
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
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null) {
                        fragmentManager.beginTransaction().hide(fragment).commit();
                    }
                }
            }
        });
        newWaiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addWaiterFragment = new AddWaiter();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null) {
                        fragmentTransaction.hide(fragment);
                    }
                }
                fragmentTransaction.replace(R.id.fragmentAddWaiter, addWaiterFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                View frameLayout = findViewById(R.id.fragmentAddWaiter);
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
                        category.setLayoutManager(new LinearLayoutManager(Stock.this, LinearLayoutManager.HORIZONTAL,false));
                        adapterListCategory = new CategoryAdapter(list);
                        category.setAdapter(adapterListCategory);

                        //Dohvačanje ID a od odabrane kategorije
                        CategoryAdapter adapterListCategory = new CategoryAdapter(list);
                        adapterListCategory.setCategoryClickListener(Stock.this);
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
        Call<ArrayList<Article>> call = service.getAllArticles();
        Call<ArrayList<com.example.onlinekonobar.Api.Stock>> callStock = service.getAllStock();

        call.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(Call<ArrayList<Article>> call, Response<ArrayList<Article>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Article> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        if (isSearch) {
                            list = filterArticles(list);
                        } else if (catId != 0) {
                            list = filterArticlesByCategory(list, catId);
                        }

                        final ArrayList<Article> finalList = list;

                        callStock.enqueue(new Callback<ArrayList<com.example.onlinekonobar.Api.Stock>>() {
                            @Override
                            public void onResponse(Call<ArrayList<com.example.onlinekonobar.Api.Stock>> call, Response<ArrayList<com.example.onlinekonobar.Api.Stock>> response) {
                                if (response.isSuccessful()) {
                                    ArrayList<com.example.onlinekonobar.Api.Stock> listStock = response.body();
                                    if (listStock != null && !listStock.isEmpty()) {
                                        article.setLayoutManager(new GridLayoutManager(Stock.this, 1));
                                        adapterListAdminDrink = new StockAdminAdapter(finalList, listStock, Stock.this, idUser);
                                        article.setAdapter(adapterListAdminDrink);
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Call<ArrayList<com.example.onlinekonobar.Api.Stock>> call, Throwable throwable) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    }
                } else {
                    // Handle unsuccessful response
                    progressBar.setVisibility(View.GONE);
                }
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