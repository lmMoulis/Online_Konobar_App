package com.example.onlinekonobar.Activity.User;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlinekonobar.Activity.User.Adapter.CustomizeUserAdapter;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Normative;
import com.example.onlinekonobar.Api.Stock;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.Activity.User.Adapter.ManagementCart;
import com.example.onlinekonobar.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailArticles extends Fragment {
    private Article object;
    private UserService userService;
    Context context;
    ImageView image, minus, plus;
    TextView title, price, volumen, description, totalPrice, numberItem, buyNow, addToCart;
    int number=1;
    int ArticleCatId;
    CustomizeUserAdapter adapterCustomize;
    ManagementCart managementCart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_articles, container, false);

        title = view.findViewById(R.id.titleTxt);
        price = view.findViewById(R.id.priceTxt);
        volumen = view.findViewById(R.id.volumenTxt);
        description = view.findViewById(R.id.descriptionTxt);
        totalPrice = view.findViewById(R.id.totalPriceTxt);
        numberItem = view.findViewById(R.id.numberTxt);
        buyNow = view.findViewById(R.id.buyNowBtn);
        addToCart = view.findViewById(R.id.addToCartBtn);
        image = view.findViewById(R.id.img);
        minus=view.findViewById(R.id.minusBtn);
        plus=view.findViewById(R.id.stockPlusBtn);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int idUser = sharedPreferences.getInt("userId", -1);

        // Dohvat ID prijavljenog korisnika iz Intent-a ako postoji
        Bundle args = getArguments();
        if (args != null) {
            int userIdFromIntent = args.getInt("userId", -1);
        }


        if (getArguments() != null) {
            object = (Article) getArguments().getSerializable("selected_article");
            setVariable();
            ArticleCatId=object.getKategorija_Id();
            context=getContext();
        }
        managementCart=new ManagementCart(context);
        userService=Client.getService();

        plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkStockAndUpdateQuantity(object.getId(), true);
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(number !=1)
                {
                    checkStockAndUpdateQuantity(object.getId(), false);

                }
                else {
                    Toast.makeText(getActivity(), "Količina ne može biti manja od 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_customize);
                RecyclerView customRecyclerView = dialog.findViewById(R.id.customeRecyclerView);
                Button confirmBtn = dialog.findViewById(R.id.saveBtn);

                if (customRecyclerView != null) {
                    customRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    initDialogList(customRecyclerView, object.getKategorija_Id());

                    // Postavljanje onClickListenera za gumb za potvrdu unutar dijaloga
                    confirmBtn.setOnClickListener(confirmView -> {
                        if (adapterCustomize != null) {
                            Customize selectedCustomize = adapterCustomize.getSelectedCustomize();
                            if (selectedCustomize != null) {
                                int userId = idUser;
                                int documentId = 1;
                                managementCart.insertArticle(object, selectedCustomize, number, userId, documentId);

                            }
                            Log.e("ArticleUserAdapter", "Artikal dodan");
                        } else {
                            Log.e("ArticleUserAdapter", "AdapterCustomize is null");
                        }
                        dialog.dismiss();
                    });
                    dialog.show();
                } else {
                    Log.e("ArticleUserAdapter", "Failed to initialize customRecyclerView");
                }
            }
        });
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_customize);
                RecyclerView customRecyclerView = dialog.findViewById(R.id.customeRecyclerView);
                Button confirmBtn = dialog.findViewById(R.id.saveBtn);
                if (customRecyclerView != null) {
                    customRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    initDialogList(customRecyclerView, object.getKategorija_Id());

                    // Postavljanje onClickListenera za gumb za potvrdu unutar dijaloga
                    confirmBtn.setOnClickListener(confirmView -> {
                        if (adapterCustomize != null) {
                            Customize selectedCustomize = adapterCustomize.getSelectedCustomize();
                            if (selectedCustomize != null) {
                                int userId = 1;
                                int documentId = 1;
                                managementCart.insertArticle(object, selectedCustomize, number, userId, documentId);

                            }
                            Log.e("ArticleUserAdapter", "Artikal dodan");

                        } else {
                            Log.e("ArticleUserAdapter", "AdapterCustomize is null");
                        }
                        dialog.dismiss();
                        Fragment cartFragment = new Card();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentDetailArticlesUser, cartFragment);
                        fragmentTransaction.commit();
                        getParentFragmentManager().executePendingTransactions();
                        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentDetailArticlesUser);
                        View frameLayout = currentFragment.getView().findViewById(R.id.cardfragment);
                        frameLayout.setVisibility(View.VISIBLE);
                    });
                    dialog.show();
                } else {
                    Log.e("ArticleUserAdapter", "Failed to initialize customRecyclerView");
                }
            }
        });

        return view;
    }
    private void initDialogList(RecyclerView recyclerView, int catId) {
        UserService service = Client.getService();
        Call<ArrayList<Customize>> call = service.getAllCustomize();
        call.enqueue(new Callback<ArrayList<Customize>>() {
            @Override
            public void onResponse(Call<ArrayList<Customize>> call, Response<ArrayList<Customize>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Customize> list = response.body();
                    ArrayList<Customize> filteredList = new ArrayList<>();
                    for (Customize customize : list) {
                        if(Objects.equals(customize.getId_Kategorije(), catId))
                        {
                            filteredList.add(customize);
                        }
                    }
                    Log.d("Category", "If : ");
                    adapterCustomize = new CustomizeUserAdapter(filteredList);
                    recyclerView.setAdapter(adapterCustomize);

                } else {
                    // Handle unsuccessful response
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Customize>> call, Throwable t) {
                // Handle failure
            }
        });
    }
    private void checkStockAndUpdateQuantity(int articleId, boolean increment) {
        userService.getNormativeByArticleId(articleId).enqueue(new Callback<ArrayList<Normative>>() {
            @Override
            public void onResponse(Call<ArrayList<Normative>> call, Response<ArrayList<Normative>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Normative> existingNormatives = response.body();
                    if (existingNormatives != null) {
                        int totalNormatives = existingNormatives.size();
                        int[] checkedNormatives = {0};
                        boolean[] canUpdate = {true};

                        for (Normative normative : existingNormatives) {
                            userService.getStockById(normative.Skladiste_Id).enqueue(new Callback<Stock>() {
                                @Override
                                public void onResponse(Call<Stock> call, Response<Stock> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Stock stock = response.body();
                                        int totalNormativeQuantity = normative.getNormativ() * (number + (increment ? 1 : -1));
                                        if (increment && totalNormativeQuantity > stock.getKolicina()) {
                                            canUpdate[0] = false;
                                        } else if (!increment && number <= 1) {
                                            canUpdate[0] = false;
                                        }
                                    } else {
                                        canUpdate[0] = false;
                                    }
                                    checkedNormatives[0]++;
                                    if (checkedNormatives[0] == totalNormatives) {
                                        updateQuantityIfPossible(increment, canUpdate[0]);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Stock> call, Throwable t) {
                                    Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(context, "Greška u dobavljanju normativa.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Greška prilikom provjere normativa.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Normative>> call, Throwable t) {
                Toast.makeText(context, "Greška u komunikaciji sa serverom.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateQuantityIfPossible(boolean increment, boolean canUpdate) {
        if (canUpdate) {
            if (increment) {
                number++;
            } else {
                number--;
            }
            numberItem.setText(number + "");
            totalPrice.setText(String.format("%.2f", number * object.getCijena()) + "€");
        } else {
            Toast.makeText(context, "Nedovoljno artikala na skladištu.", Toast.LENGTH_SHORT).show();
        }
    }
    public void setVariable() {
        if (object != null) {
            title.setText(object.getNaziv());
            price.setText(String.format("%.2f",object.getCijena()) + "€");
            volumen.setText(object.getKolicina() +" ml");
            numberItem.setText(number+"");
            totalPrice.setText(String.format("%.2f",object.getCijena() )+ "€");

            Glide.with(this)
                    .load(object.getSlika())
                    .into(image);
        }
    }
}
