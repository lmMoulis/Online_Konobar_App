package com.example.onlinekonobar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.onlinekonobar.Activity.User.Articles;
import com.example.onlinekonobar.Api.Article;
import com.example.onlinekonobar.Api.Customize;
import com.example.onlinekonobar.Api.Item;
import com.example.onlinekonobar.TinyDB;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertArticle(Article article, Customize customize, int quantity, int userId, int documentId) {
        ArrayList<Item> cartList = getListCart();

        // Provjerite je li cartList null
        if (cartList == null) {
            cartList = new ArrayList<>(); // Inicijalizirajte novu listu ako je null
        }

        boolean existAlready = false;
        int index = 0;

        for (int i = 0; i < cartList.size(); i++) {
            Item item = cartList.get(i);
            if (item.getArtikal_Id() == article.getId() && item.getDodatak() == customize.getId()) {
                existAlready = true;
                index = i;
                break;
            }
        }

        if (existAlready) {
            cartList.get(index).setKolicina(cartList.get(index).getKolicina() + quantity);
        } else {
            Item newItem = new Item();
            newItem.setArtikal_Id(article.getId());
            newItem.setKorisnik_Id(userId);
            newItem.setDokument_Id(documentId);
            newItem.setKolicina(quantity);
            newItem.setDodatak(customize.getId());
            cartList.add(newItem);
            Log.d("ManagementCart", "Added new item to cart: " + newItem.getArtikal_Id() + ", " + newItem.getDodatak() + ", " + newItem.getKolicina());
        }

        tinyDB.putListObject("CartList", cartList);
        Toast.makeText(context, "Artikal je dodan u košaricu", Toast.LENGTH_SHORT).show();
    }


    public void deleteArticle(int position) {
        ArrayList<Item> cartList = getListCart();
        if (position >= 0 && position < cartList.size()) {
            cartList.remove(position);
            tinyDB.putListObject("CartList", cartList);
        }
    }

    public void clearCart() {
        ArrayList<Item> emptyList = new ArrayList<>();
        tinyDB.putListObject("CartList", emptyList);
        Toast.makeText(context, "Košarica je očišćena", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Item> getListCart() {
        return tinyDB.getListObject("CartList", Item.class);
    }

    public Double getTotalFee() {
        ArrayList<Item> cartList = getListCart();
        double total = 0;

        for (Item item : cartList) {
            Article article = getArticleById(item.getArtikal_Id());
            if (article != null) {
                total += article.getCijena() * item.getKolicina();
            }
        }

        return total;
    }

    public Article getArticleById(int articleId) {

        // Pretpostavimo da imamo metodu za dohvaćanje artikla prema ID-u
        // Ovdje bi trebali implementirati način kako dohvatiti artikl prema ID-u
        return null;
    }
}
