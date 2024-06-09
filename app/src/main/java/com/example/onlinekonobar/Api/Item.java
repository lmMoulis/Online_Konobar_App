package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Item implements Serializable {
    @SerializedName("id")
    public int Id;
    @SerializedName("order_Id")
    public String Order_Id;
    @SerializedName("artikal_Id")
    public int Artikal_Id;
    @SerializedName("korisnik_Id")
    public int Korisnik_Id;
    @SerializedName("dokument_Id")
    public int Dokument_Id;
    @SerializedName("kolicina")
    public int Kolicina;
    @SerializedName("dodatak_Id")
    public int Dodatak;
    @SerializedName("cijena")
    public float Cijena;
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getOrder_Id() {
        return Order_Id;
    }

    public void setOrder_Id(String order_Id) {
        Order_Id = order_Id;
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

    public float getCijena() {
        return Cijena;
    }

    public void setCijena(float cijena) {
        Cijena = cijena;
    }
}
