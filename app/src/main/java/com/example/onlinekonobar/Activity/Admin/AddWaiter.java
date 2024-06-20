package com.example.onlinekonobar.Activity.Admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinekonobar.Activity.Admin.Adapter.AddWaiterAdapter;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Client;
import com.example.onlinekonobar.Api.Invoice;
import com.example.onlinekonobar.Api.User;
import com.example.onlinekonobar.Api.UserService;
import com.example.onlinekonobar.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddWaiter extends Fragment {
    RecyclerView.Adapter usersAdapter;
    private RecyclerView users;
    private String searchText;
    private Boolean isSearch=false;
    EditText inputSearch;
    ImageView searchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_waiter, container, false);

        users=view.findViewById(R.id.allPeopleRecycler);
        inputSearch=view.findViewById(R.id.peopleSearchInp);
        searchBtn=view.findViewById(R.id.adminPeopleSearchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText=inputSearch.getText().toString();
                isSearch =!searchText.isEmpty();
                initList();
            }
        });
        initList();
        return view;
    }
    public void initList()
    {
        UserService service = Client.getService();
        Call<ArrayList<User>> call = service.getAllUsers();
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful()) {
                    ArrayList<User> usersList = response.body();
                    if (usersList != null && !usersList.isEmpty()) {
                        if (usersList != null && !usersList.isEmpty()) {
                            if (isSearch) {
                                usersList = filterUsers(usersList);
                            }

                        }
                        users.setLayoutManager(new GridLayoutManager(getContext(),1));
                        usersAdapter=new AddWaiterAdapter(usersList,getContext());
                        users.setAdapter(usersAdapter);


                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable throwable) {

            }
        });
    }
    private ArrayList<User> filterUsers(ArrayList<User> users) {
        ArrayList<User> filteredList = new ArrayList<>();
        for (User user : users) {
            if (user.getEmail().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(user);
            }
        }
        return filteredList;
    }
}