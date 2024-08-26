package com.example.onlinekonobar.Api;

import com.google.gson.annotations.SerializedName;

public class Remaining {
    @SerializedName("artikalId")
    public int Artikal_Id;
    @SerializedName("artikalNaziv")
    public String Artikal_Naziv;
    @SerializedName("daysRemainingText")
    public String Days_Remaining;

    public int getArtikal_Id() {
        return Artikal_Id;
    }

    public void setArtikal_Id(int artikal_Id) {
        Artikal_Id = artikal_Id;
    }

    public String getArtikal_Naziv() {
        return Artikal_Naziv;
    }

    public void setArtikal_Naziv(String artikal_Naziv) {
        Artikal_Naziv = artikal_Naziv;
    }

    public String getDays_Remaining() {
        return Days_Remaining;
    }

    public void setDays_Remaining(String days_Remaining) {
        Days_Remaining = days_Remaining;
    }
}
