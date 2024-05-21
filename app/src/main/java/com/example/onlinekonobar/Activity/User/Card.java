package com.example.onlinekonobar.Activity.User;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinekonobar.Adapter.ArticleUserAdapter;
import com.example.onlinekonobar.Adapter.CardAdapter;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.ManagementCart;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Card extends Fragment {

    private RecyclerView.Adapter adapterCardElement;
    private ManagementCart managementCart;
    RecyclerView cardElement;
    ArrayList<Article> cartArticles;
    ArrayList<Customize> cartCustomizes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);

        managementCart = new ManagementCart(getContext());
        cardElement=view.findViewById(R.id.listCardRecycler);
        // Ispisujemo podatke iz ManagementCart u log
        ArrayList<Item> cartList = managementCart.getListCart();
        if (cartList != null && !cartList.isEmpty()) {
            for (Item item : cartList) {
                Log.d("ManagementCart", "Artikal ID: " + item.getArtikal_Id() + ", Količina: " + item.getKolicina() +"Id dodatka" +item.getDodatak());
            }
        }
        initList();

        return view;
    }

    private void initList() {
        UserService service = Client.getService();
        Call<ArrayList<Article>> call = service.getAllArticles();
        call.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(Call<ArrayList<Article>> call, Response<ArrayList<Article>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Article> allArticles = response.body();
                    if (allArticles != null && !allArticles.isEmpty()) {
                        ArrayList<Item> cartList = managementCart.getListCart();
                        if (cartList != null && !cartList.isEmpty()) {
                            cartArticles = new ArrayList<>();
                            for (Item item : cartList) {
                                Article article = findArticleById(allArticles, item.getArtikal_Id());
                                if (article != null) {
                                    cartArticles.add(article);
                                }
                            }
                            cardElement.setLayoutManager(new GridLayoutManager(getContext(), 1));
                            adapterCardElement = new CardAdapter(cartArticles, getContext());
                            cardElement.setAdapter(adapterCardElement);
                            Log.d("ManagementCart", "Test " + cartArticles);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Article>> call, Throwable throwable) {
                // Handle failure
            }
        });
    }
    private Article findArticleById(ArrayList<Article> allArticles, int articleId) {
        for (Article article : allArticles) {
            if (article.getId() == articleId) {
                return article;
            }
        }
        return null;
    }

}