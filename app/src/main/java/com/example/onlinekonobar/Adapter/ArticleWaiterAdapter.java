package com.example.onlinekonobar.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.ManagementCart;
import com.example.onlinekonobar.R;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleWaiterAdapter extends RecyclerView.Adapter<ArticleWaiterAdapter.viewholder> {
    ArrayList<Article>items;
    Context context;
    private int idUser;
    ManagementCart managementCart;
    CustomizeWaiterAdapter adapterCustomize;

    public ArticleWaiterAdapter(ArrayList<Article> items,Context context,int idUser)
    {
        this.items=items;
        this.context = context;
        this.managementCart = new ManagementCart(context);
        this.idUser=idUser;
    }


    @NonNull
    @Override
    public ArticleWaiterAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate= LayoutInflater.from(context).inflate(R.layout.viewholder_list_waiter_article,parent,false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleWaiterAdapter.viewholder holder, int position) {
        Article article = items.get(position);
        holder.title.setText(items.get(position).getNaziv());
        holder.itemView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_customize);
            RecyclerView customRecyclerView = dialog.findViewById(R.id.customeRecyclerView);
            Button confirmBtn = dialog.findViewById(R.id.saveBtn);

            if (customRecyclerView != null) {
                customRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                initDialogList(customRecyclerView, article.getKategorija_Id());

                // Postavljanje onClickListenera za gumb za potvrdu unutar dijaloga
                confirmBtn.setOnClickListener(confirmView -> {
                    if (adapterCustomize != null) {
                        Customize selectedCustomize = adapterCustomize.getSelectedCustomize();
                        if (selectedCustomize != null) {
                            int userId = idUser;
                            int documentId = 1;
                            managementCart.insertArticle(article, selectedCustomize, 1, userId, documentId);
                        }
                    } else {
                        Log.e("ArticleUserAdapter", "AdapterCustomize is null");
                    }
                    dialog.dismiss();
                });
                dialog.show();
            } else {
                Log.e("ArticleUserAdapter", "Failed to initialize customRecyclerView");
            }
        });
    }
    private void initDialogList(RecyclerView recyclerView,int catId) {
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
                    adapterCustomize = new CustomizeWaiterAdapter(filteredList);
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

    @Override
    public int getItemCount() {return items.size();}
    public class viewholder extends RecyclerView.ViewHolder{
        TextView title;
        public viewholder(@NonNull View itemView)
        {
            super(itemView);
            title=itemView.findViewById(R.id.waiterTitleTxt);
        }
    }
}
