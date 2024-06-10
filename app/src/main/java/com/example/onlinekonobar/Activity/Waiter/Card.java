package com.example.onlinekonobar.Activity.Waiter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.onlinekonobar.Activity.Waiter.Adapter.CartWaiterAdapter;
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
    TextView empty, subtotalTxt, vatTxt, totalTxt;
    Button pay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_waiter, container, false);
        managementCart = new ManagementCart(getContext());

        managementCart.setUpdateTotalFeeCallback(new Runnable() {
            @Override
            public void run() {
                updateTotalFee();
            }
        });
        cardElement = view.findViewById(R.id.listCardRecyclerWaiter);
        empty = view.findViewById(R.id.emptyCartWaiterTxt);
        subtotalTxt = view.findViewById(R.id.subtotalWaiterTxt);
        vatTxt = view.findViewById(R.id.vatWaiterTxt);
        totalTxt = view.findViewById(R.id.totalWaiterTxt);
        pay=view.findViewById(R.id.payWaiterBtn);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Card","User id"+getUserId(managementCart.getListCart()));
                managementCart.saveCartToDatabase(getUserId(managementCart.getListCart()));
            }
        });

        initList();
        updateTotalFee();

        return view;
    }
    private void updateTotalFee() {
        managementCart.getTotalFee(new ManagementCart.TotalFeeCallback() {
            @Override
            public void onTotalFeeCalculated(double totalFee) {
                subtotalTxt.setText(String.format("%.2f", totalFee * 0.75));
                vatTxt.setText(String.format("%.2f", totalFee * 0.25));
                totalTxt.setText(String.format("%.2f", totalFee));
            }
        });
    }
    private void initList() {
        UserService service = Client.getService();
        Call<ArrayList<Article>> callArticles = service.getAllArticles();
        Call<ArrayList<Customize>> callCustomizes = service.getAllCustomize();
        callArticles.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(Call<ArrayList<Article>> call, Response<ArrayList<Article>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Article> allArticles = response.body();
                    if (allArticles != null && !allArticles.isEmpty()) {
                        callCustomizes.enqueue(new Callback<ArrayList<Customize>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Customize>> call, Response<ArrayList<Customize>> response) {
                                if (response.isSuccessful()) {
                                    ArrayList<Customize> allCustomizes = response.body();
                                    if (allCustomizes != null && !allCustomizes.isEmpty()) {
                                        ArrayList<Item> cartList = managementCart.getListCart();
                                        if (cartList != null && !cartList.isEmpty()) {
                                            cartArticles = new ArrayList<>();
                                            cartCustomizes = new ArrayList<>();
                                            for (Item item : cartList) {
                                                Article article = findArticleById(allArticles, item.getArtikal_Id());
                                                Customize customize = getCustomizeById(allCustomizes, item.getDodatak());
                                                if (article != null) {
                                                    cartArticles.add(article);
                                                    cartCustomizes.add(customize);
                                                }
                                            }
                                            cardElement.setLayoutManager(new GridLayoutManager(getContext(), 1));
                                            adapterCardElement = new CartWaiterAdapter(cartArticles, cartCustomizes, getContext(), managementCart, new Runnable() {
                                                @Override
                                                public void run() {
                                                    checkEmptyState();
                                                }
                                            });
                                            cardElement.setAdapter(adapterCardElement);
                                            checkEmptyState();
                                            updateTotalFee();
                                        } else {
                                            checkEmptyState();
                                            updateTotalFee();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Customize>> call, Throwable throwable) {
                                // Handle failure
                            }
                        });
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

    private Customize getCustomizeById(ArrayList<Customize> allCustomize, int customizeId) {
        for (Customize customize : allCustomize) {
            if (customize.getId() == customizeId) {
                return customize;
            }
        }
        return null;
    }

    private void checkEmptyState() {
        if (adapterCardElement != null && adapterCardElement.getItemCount() != 0) {
            cardElement.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
            cardElement.setVisibility(View.GONE);
        }
    }

    private int getUserId(ArrayList<Item> itemArticles)
    {
        for(Item item:itemArticles)
        {
            int userId=item.getKorisnik_Id();
            return userId;
        }
        return  -1;
    }
}