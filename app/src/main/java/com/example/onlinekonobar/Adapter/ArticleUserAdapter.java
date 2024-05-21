package com.example.onlinekonobar.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.onlinekonobar.Activity.User.Articles;
import com.example.onlinekonobar.Activity.User.DetailArticles;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Category;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.ManagementCart;
import com.example.onlinekonobar.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleUserAdapter extends RecyclerView.Adapter<ArticleUserAdapter.viewholder> {

    ArrayList<Article> items;
    Context context;
    int ArticleCatId;
    Button save;
    ManagementCart managementCart;

    CustomizeAdapter adapterCustomize;


    public ArticleUserAdapter(ArrayList<Article> items,Context context) {
        this.items = items;
        this.context = context;
        this.managementCart = new ManagementCart(context);
    }

    @NonNull
    @Override
    public ArticleUserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_article,parent,false);
        return new viewholder(inflate);
    }


    @Override
    public void onBindViewHolder(@NonNull ArticleUserAdapter.viewholder holder, int position) {
        Article article = items.get(position);
        holder.title.setText(items.get(position).getNaziv());
        holder.price.setText(items.get(position).getCijena()+"€");
        Glide.with(context)
                .load(items.get(position).getSlika())
                .transform(new FitCenter(),new RoundedCorners(20))
                .into(holder.img);
        //Dohvacanje positiona za pritisnuti element
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selected_article", article);
            DetailArticles detailFragment = new DetailArticles();
            detailFragment.setArguments(bundle);

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentDetailArticlesUser, detailFragment)
                    .addToBackStack(null)
                    .commit();
            View frameLayout = ((FragmentActivity) context).findViewById(R.id.fragmentDetailArticlesUser);
            frameLayout.setVisibility(View.VISIBLE);
        });

        //Otvaranje Dialoga pritiskom na Plus gumb
        holder.addBtn.setOnClickListener(v -> {
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
                            int userId = 1;
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
                        adapterCustomize = new CustomizeAdapter(filteredList);
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
    public int getItemCount() {
        return items.size();
    }



    public class viewholder  extends  RecyclerView.ViewHolder{
        TextView title,price,addBtn;
        ImageView img;
        public viewholder(@NonNull View itemView){
            super(itemView);

            title=itemView.findViewById(R.id.userTitleTxt);
            price=itemView.findViewById(R.id.userPriceTxt);
            addBtn=itemView.findViewById(R.id.userAddBtn);
            img=itemView.findViewById(R.id.userImg);
        }
    }
}
