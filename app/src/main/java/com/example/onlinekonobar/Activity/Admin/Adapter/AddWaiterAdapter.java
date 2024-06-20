package com.example.onlinekonobar.Activity.Admin.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddWaiterAdapter extends RecyclerView.Adapter<AddWaiterAdapter.viewholder>{
    private ArrayList<User>usersItems;
    private Context context;

    public AddWaiterAdapter(ArrayList<User>usersItems,Context context)
    {
        this.usersItems=usersItems;
        this.context=context;
    }
    @NonNull
    @Override
    public AddWaiterAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_add_waiter, parent, false);
        return new AddWaiterAdapter.viewholder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull AddWaiterAdapter.viewholder holder, int position) {
        User user = usersItems.get(position);
        holder.name.setText(user.getIme()+" "+ user.getPrezime());
        holder.email.setText(String.valueOf(user.getEmail()));
        holder.birthDate.setText(String.valueOf(convertDateFormat(user.getDatum_Rodenja())));
        Glide.with(context)
                .load(usersItems.get(position).getSlika())
                .transform(new FitCenter(),new RoundedCorners(20))
                .into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            // Prikazivanje AlertDialog-a
            new AlertDialog.Builder(context)
                    .setTitle("Dodaj novog konobara")
                    .setMessage("Jeste li sigurni da želite postaviti konobara?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Poziv API-ja s PUT metodom
                            UserService userService = Client.getService();
                            user.setIme(user.getIme());
                            user.setPrezime(user.getPrezime());
                            user.setEmail(user.getEmail());
                            user.setLozinka(user.getLozinka());
                            user.setBroj_Mobitela(user.getBroj_Mobitela());
                            user.setSpol(user.getSpol());
                            user.setDatum_Rodenja(user.getDatum_Rodenja());
                            user.setSlika(user.getSlika());
                            user.setPristup(2);

                            Call<Void> call = userService.setUserImage(user.getId(), user);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, "Korisnik je dodijeljen kao konobar.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable throwable) {

                                }
                            });
                        }
                    })
                    .setNegativeButton("Ne", null)
                    .show();
        });

    }
    @Override
    public int getItemCount() {
        return usersItems.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView name,email, birthDate;
        ImageView img;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.addWaiterNameTxt);
            email=itemView.findViewById(R.id.addWaiterEmailTxt);
            birthDate=itemView.findViewById(R.id.addWaiterBirthTxt);
            img=itemView.findViewById(R.id.profilePicturePeople);


        }
    }
    public String convertDateFormat(String date) {
        String newDateFormat = "";
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Log.d("convertDateFormat: ","Datum"+targetFormat);
        try {
            Date originalDate = originalFormat.parse(date);
            newDateFormat = targetFormat.format(originalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateFormat;
    }
}
