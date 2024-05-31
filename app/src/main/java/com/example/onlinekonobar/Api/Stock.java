package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Stock implements Serializable {
    @SerializedName("artikal_id")
    public int Artikal_Id;
    @SerializedName("dokument_id")
    public int Dokument_Id;
    @SerializedName("korisnik_id")
    public int Korisnik_Id;
    @SerializedName("kolicina")
    public int Kolicina;

    public int getArtikal_Id() {
        return Artikal_Id;
    }

    public void setArtikal_Id(int artikal_Id) {
        Artikal_Id = artikal_Id;
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
}
