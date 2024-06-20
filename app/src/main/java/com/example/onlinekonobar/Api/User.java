package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("id")
    private  int Id;
    @SerializedName("ime")
    private String Ime;
    @SerializedName("prezime")
    private String Prezime;
    @SerializedName("email")
    private String Email;
    @SerializedName("lozinka")
    private String Lozinka;
    @SerializedName("broj_Mobitela")
    private String Broj_Mobitela;
    @SerializedName("spol")
    private String Spol;
    @SerializedName("datum_Rodenja")
    private String Datum_Rodenja;
    @SerializedName("pristup")
    private int Pristup;
    @SerializedName("slika")
    private String Slika;
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getIme() {
        return Ime;
    }

    public void setIme(String ime) {
        Ime = ime;
    }

    public String getPrezime() {
        return Prezime;
    }

    public void setPrezime(String prezime) {
        Prezime = prezime;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getLozinka() {
        return Lozinka;
    }

    public void setLozinka(String lozinka) {
        Lozinka = lozinka;
    }

    public String getBroj_Mobitela() {
        return Broj_Mobitela;
    }

    public void setBroj_Mobitela(String broj_Mobitela) {
        Broj_Mobitela = broj_Mobitela;
    }

    public String getSpol() {
        return Spol;
    }

    public void setSpol(String spol) {
        Spol = spol;
    }

    public String getDatum_Rodenja() {
        return Datum_Rodenja;
    }

    public void setDatum_Rodenja(String datum_Rodenja) {
        Datum_Rodenja = datum_Rodenja;
    }

    public int getPristup() {
        return Pristup;
    }

    public void setPristup(int pristup) {
        Pristup = pristup;
    }

    public String getSlika() {
        return Slika;
    }

    public void setSlika(String slika) {
        Slika = slika;
    }
}

