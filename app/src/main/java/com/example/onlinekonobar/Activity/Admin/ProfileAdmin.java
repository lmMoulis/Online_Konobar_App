package com.example.onlinekonobar.Activity.Admin;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlinekonobar.Activity.Login;
import com.example.onlinekonobar.Activity.Waiter.Chart;
import com.example.onlinekonobar.Activity.Waiter.InvoiceList;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileAdmin extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    TextView name,countOrder,email,date,password,logout;
    ImageView order,document,profilePicture,chartBtn;
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
//        gender=view.findViewById(R.id.genderProfileAdminTxt);
        password=view.findViewById(R.id.passwordProfileAdminTxt);
        order=view.findViewById(R.id.getAdminOrders);
        document=view.findViewById(R.id.takeDocumentAdmin);
//        takeOrder=view.findViewById(R.id.takeOrderAdmin);
        chartBtn=view.findViewById(R.id.takeStatisticAdmin);
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
                Fragment invoiceListFragment = new InvoiceListAdmin();
                FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null) {
                        fragmentTransaction.hide(fragment);
                    }
                }
                fragmentTransaction.replace(R.id.fragmentInvoiceListAdmin, invoiceListFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment documentFragment = new DocumentList();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.profileFragmentAdmin, documentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        chartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment barChart = new Chart();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.profileFragmentAdmin, barChart);
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
//        getAllOrder();
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
//        gender.setText(user.getSpol());
        if (user.getSlika() != null && !user.getSlika().isEmpty()) {
            Glide.with(requireContext())
                    .load(user.getSlika())
                    .fitCenter()
                    .circleCrop()
                    .into(profilePicture);
        } else {
            profilePicture.setImageResource(R.drawable.user_profile);
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

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Odaberi opciju")
                .setItems(new CharSequence[]{"Uslikaj sliku", "Odaberi iz galerije"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchTakePictureIntent();
                                break;
                            case 1:
                                openGallery();
                                break;
                        }
                    }
                })
                .create()
                .show();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                uploadImageToFirebase(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }
    private void uploadImageToFirebase(Bitmap bitmap) {
        StorageReference profileRef = storageReference.child("images/" + idUser + "/profile.jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Greška pri spremanju slike", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        updateUserProfilePicture(downloadUrl);
                    }
                });
            }
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference profileRef = storageReference.child("profle_picture/" + idUser + "/profile.jpg");

        UploadTask uploadTask = profileRef.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Greška pri spremanju slike", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        updateUserProfilePicture(downloadUrl);
                    }
                });
            }
        });
    }
    private void updateUserProfilePicture(String downloadUrl) {

        UserService userService = Client.getService();
        Call<User> call = userService.getUserById(idUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        Log.d("Profile","URL "+user.getPrezime());
                        user.setIme(user.getIme());
                        user.setPrezime(user.getPrezime());
                        user.setEmail(user.getEmail());
                        user.setLozinka(user.getLozinka());
                        user.setBroj_Mobitela(user.getBroj_Mobitela());
                        user.setSpol(user.getSpol());
                        user.setDatum_Rodenja(user.getDatum_Rodenja());
                        user.setPristup(user.getPristup());
                        user.setSlika(downloadUrl);

                        Log.d("Profile","URL "+downloadUrl);
                        Call<Void> updateCall = userService.setUserImage(idUser, user);
                        updateCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Slika je uspješno ažurirana", Toast.LENGTH_SHORT).show();
                                    updateUI(user);
                                    progressBar.setVisibility(GONE);
                                } else {
                                    Toast.makeText(getContext(), "Greška pri ažuriranju slike", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(), "Greška u komunikaciji sa serverom", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(GONE);
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Greška pri dohvaćanju korisnika", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Greška u komunikaciji sa serverom", Toast.LENGTH_SHORT).show();
            }
        });
    }
}