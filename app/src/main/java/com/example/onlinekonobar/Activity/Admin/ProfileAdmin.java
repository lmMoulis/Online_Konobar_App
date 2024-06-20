package com.example.onlinekonobar.Activity.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlinekonobar.Activity.Login;
import com.example.onlinekonobar.Activity.Waiter.InvoiceList;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileAdmin extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    TextView name,countOrder,email,date,gender,password,logout;
    ImageView order,takeOrder,profilePicture;
    int orderNum;
    ProgressBar progressBar;
    int idUser;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile_admin,container,false);

        name=view.findViewById(R.id.nameSurnameAdminTxt);
        countOrder=view.findViewById(R.id.countOrderAdminTxt);
        email=view.findViewById(R.id.emailProfileAdminTxt);
        date=view.findViewById(R.id.dateProfileAdminTxt);
        gender=view.findViewById(R.id.genderProfileAdminTxt);
        password=view.findViewById(R.id.passwordProfileAdminTxt);
        order=view.findViewById(R.id.getAdminOrders);
        takeOrder=view.findViewById(R.id.takeOrderAdmin);
        progressBar=view.findViewById(R.id.progressBarAdminProfile);
        logout=view.findViewById(R.id.logoutAdminBtn);
        profilePicture=view.findViewById(R.id.profileAdminPic);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to Login activity
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                getActivity().finish(); // Close current activity

            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment invoiceFragment = new InvoiceListAdmin();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.profileFragmentWaiter, invoiceFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        profilePicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOptionsDialog();
                return true;
            }
        });
        initList();
        getAllOrder();
        return view;
    }

    private void initList() {
        progressBar.setVisibility(View.VISIBLE);
        UserService userService= Client.getService();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt("userId", -1);
        userService.getUserById(idUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        updateUI(user);
                    } else {
                        Toast.makeText(getContext(), "Korisnik nije pronađen", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Greška pri dohvaćanju podataka", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Greška u komunikaciji sa serverom", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void updateUI(User user)
    {
        countOrder.setText(orderNum+"");
        name.setText(user.getIme()+" "+user.getPrezime());
        email.setText(user.getEmail());
        date.setText(convertDateFormat(user.getDatum_Rodenja()));
        gender.setText(user.getSpol());
        Glide.with(requireContext())
                .load(user.getSlika())
                .fitCenter()
                .circleCrop()
                .into(profilePicture);
    }
}