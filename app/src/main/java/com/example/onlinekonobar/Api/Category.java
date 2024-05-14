package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    public int Id;
    @SerializedName("naziv")
    private  String Naziv;


    public Category(String naziv) {
        this.Naziv = naziv;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNaziv() {
        return Naziv;
    }

    public void setNaziv(String naziv) {
        Naziv = naziv;
    }
}
