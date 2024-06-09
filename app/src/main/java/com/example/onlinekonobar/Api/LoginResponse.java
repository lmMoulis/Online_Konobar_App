package com.example.onlinekonobar.Api;

public class LoginResponse {
    private  int id;
    private String email;
    private String lozinka;
    private int pristup;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public int getPristup() {
        return pristup;
    }

    public void setPristup(int pristup) {
        this.pristup = pristup;
    }
}