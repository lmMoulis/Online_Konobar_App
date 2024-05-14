package com.example.onlinekonobar.Api;

public class LoginRequest {
    private String email;
    private String password;
    private int pristup;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPristup() {
        return pristup;
    }

    public void setPristup(int pristup) {
        this.pristup = pristup;
    }
}
