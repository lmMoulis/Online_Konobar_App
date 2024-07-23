package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

public class Normative {
    @SerializedName("id")
    public int Id;
    @SerializedName("artikal_Id")
    public int Artikal_Id;
    @SerializedName("normativ")
    public int Normativ;
    @SerializedName("naziv")
    public String Naziv;
    @SerializedName("skladiste_Id")
    public int Skladiste_Id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getArtikal_Id() {
        return Artikal_Id;
    }

    public void setArtikal_Id(int artikal_Id) {
        Artikal_Id = artikal_Id;
    }

    public int getNormativ() {
        return Normativ;
    }

    public void setNormativ(int normativ) {
        Normativ = normativ;
    }

    public String getNaziv() {
        return Naziv;
    }

    public void setNaziv(String naziv) {
        Naziv = naziv;
    }

    public int getSkladiste_Id() {
        return Skladiste_Id;
    }

    public void setSkladiste_Id(int skladiste_Id) {
        Skladiste_Id = skladiste_Id;
    }
}
