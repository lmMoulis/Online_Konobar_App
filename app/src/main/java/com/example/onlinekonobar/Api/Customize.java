package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

public class Customize {
    @SerializedName("id")
    public int Id;
    @SerializedName("naziv")
    public String Naziv;
    @SerializedName("id_Kategorije")
    public int Id_Kategorije;

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

    public int getId_Kategorije() {
        return Id_Kategorije;
    }

    public void setId_Kategorije(int id_Kategorije) {
        Id_Kategorije = id_Kategorije;
    }
}
