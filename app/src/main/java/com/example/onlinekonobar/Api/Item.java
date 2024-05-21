package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {
    @SerializedName("id")
    public int Id;
    @SerializedName("artikal_Id")
    public int Artikal_Id;
    @SerializedName("korisnik_Id")
    public int Korisnik_Id;
    @SerializedName("dokument_Id")
    public int Dokument_Id;
    @SerializedName("kolicina")
    public int Kolicina;
    @SerializedName("dodatak")
    public int Dodatak;


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

    public int getKorisnik_Id() {
        return Korisnik_Id;
    }

    public void setKorisnik_Id(int korisnik_Id) {
        Korisnik_Id = korisnik_Id;
    }

    public int getDokument_Id() {
        return Dokument_Id;
    }

    public void setDokument_Id(int dokument_Id) {
        Dokument_Id = dokument_Id;
    }

    public int getKolicina() {
        return Kolicina;
    }

    public void setKolicina(int kolicina) {
        Kolicina = kolicina;
    }

    public int getDodatak() {
        return Dodatak;
    }

    public void setDodatak(int dodatak) {
        Dodatak = dodatak;
    }
}
