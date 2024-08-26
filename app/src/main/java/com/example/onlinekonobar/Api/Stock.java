package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Stock implements Serializable {
    @SerializedName("id")
    public int Id;
    @SerializedName("artikal")
    public String Artikal;
    @SerializedName("dokument_id")
    public int Dokument_Id;
    @SerializedName("korisnik_id")
    public int Korisnik_Id;
    @SerializedName("kolicina")
    public int Kolicina;
    @SerializedName("slike")
    public String Slike;



    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getArtikal() {
        return Artikal;
    }

    public void setArtikal(String artikal) {
        Artikal = artikal;
    }

    public int getDokument_Id() {
        return Dokument_Id;
    }

    public void setDokument_Id(int dokument_Id) {
        Dokument_Id = dokument_Id;
    }

    public int getKorisnik_Id() {
        return Korisnik_Id;
    }

    public void setKorisnik_Id(int korisnik_Id) {
        Korisnik_Id = korisnik_Id;
    }

    public int getKolicina() {
        return Kolicina;
    }

    public void setKolicina(int kolicina) {
        Kolicina = kolicina;
    }

    public String getSlike() {
        return Slike;
    }

    public void setSlike(String slike) {
        Slike = slike;
    }
}
