package com.example.onlinekonobar.Api;
import com.google.gson.annotations.SerializedName;

public class Article {
    @SerializedName("id")
    public int Id;
    @SerializedName("naziv")
    public String Naziv;
    @SerializedName("cijena")
    public float Cijena;
    @SerializedName("kategorija_Id")
    public int Kategorija_Id;
    @SerializedName("kolicina")
    public float Kolicina;
    @SerializedName("slika")
    public String Slika;

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

    public float getCijena() {
        return Cijena;
    }

    public void setCijena(float cijena) {
        Cijena = cijena;
    }

    public int getKategorija_Id() {
        return Kategorija_Id;
    }

    public void setKategorija_Id(int kategorija_Id) {
        Kategorija_Id = kategorija_Id;
    }

    public float getKolicina() {
        return Kolicina;
    }

    public void setKolicina(float kolicina) {
        Kolicina = kolicina;
    }

    public String getSlika() {
        return Slika;
    }

    public void setSlika(String slika) {
        Slika = slika;
    }
}
