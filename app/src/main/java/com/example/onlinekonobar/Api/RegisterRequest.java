package com.example.onlinekonobar.Api;

public class RegisterRequest {
    private int Id;
    private String Ime;
    private String Prezime;
    private String Email;
    private String Lozinka;
    private String Broj_Mobitela;
    private String Spol;
    private String Datum_Rodenja;

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
}
