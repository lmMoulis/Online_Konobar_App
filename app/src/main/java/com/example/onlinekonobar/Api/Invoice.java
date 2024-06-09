package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Invoice implements Serializable {
    @SerializedName("id")
    public int Id;
    @SerializedName("dokument_Id")
    public int Dokument_Id;
    @SerializedName("broj_Racuna")
    public String Broj_Racuna;
    @SerializedName("ukupan_Iznos")
    public float Ukupan_Iznos;
    @SerializedName("datum")
    public String Datum;
   @SerializedName("korisnik_Id")
   public int Korisnik_Id;
    @SerializedName("status")
    public String Status;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getDokument_Id() {
        return Dokument_Id;
    }

    public void setDokument_Id(int dokument_Id) {
        Dokument_Id = dokument_Id;
    }

    public String getBroj_Racuna() {
        return Broj_Racuna;
    }

    public void setBroj_Racuna(String broj_Racuna) {
        Broj_Racuna = broj_Racuna;
    }

    public float getUkupan_Iznos() {
        return Ukupan_Iznos;
    }

    public void setUkupan_Iznos(float ukupan_Iznos) {
        Ukupan_Iznos = ukupan_Iznos;
    }

    public String getDatum() {
        return Datum;
    }

    public void setDatum(String datum) {
        Datum = datum;
    }

    public int getKorisnik_Id() {
        return Korisnik_Id;
    }

    public void setKorisnik_Id(int korisnik_Id) {
        Korisnik_Id = korisnik_Id;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
