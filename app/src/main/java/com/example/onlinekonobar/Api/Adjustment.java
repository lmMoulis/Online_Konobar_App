package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

public class Adjustment {
    @SerializedName("id")
    public int Id;
    @SerializedName("artikal_Id")
    public int Artikal_Id;
    @SerializedName("kolicina")
    public int Kolicina;
    @SerializedName("datum")
    public String Datum;

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

    public int getKolicina() {
        return Kolicina;
    }

    public void setKolicina(int kolicina) {
        Kolicina = kolicina;
    }

    public String getDatum() {
        return Datum;
    }

    public void setDatum(String datum) {
        Datum = datum;
    }
}
