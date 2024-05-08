package com.example.onlinekonobar.Fragment.User;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.onlinekonobar.Adapter.CategoryAdapter;
import com.example.onlinekonobar.Domain.CategoryDomain;
import com.example.onlinekonobar.R;
import java.util.ArrayList;

public class Items extends Fragment {
    private RecyclerView.Adapter catAdapter;
    private RecyclerView recyclerViewCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        // Inicijaliziraj RecyclerView
        recyclerViewCategory = rootView.findViewById(R.id.recyclerViewCat);
        initRecyclerviewCat();

        return rootView;
    }

    private void initRecyclerviewCat() {
        // Kreiraj listu kategorija
        ArrayList<CategoryDomain> items = new ArrayList<>();
        items.add(new CategoryDomain("Topli napitci"));
        items.add(new CategoryDomain("Sokovi"));
        items.add(new CategoryDomain("Pivo"));
        items.add(new CategoryDomain("Vino"));

        // Kreiraj adapter i postavi ga na RecyclerView
        CategoryAdapter adapter = new CategoryAdapter(items);
        recyclerViewCategory.setAdapter(adapter);

        // Postavi layout manager (npr. LinearLayoutManager s horizontalnom orijentacijom)
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        catAdapter=new CategoryAdapter(items);
        recyclerViewCategory.setAdapter(catAdapter);
    }
}
