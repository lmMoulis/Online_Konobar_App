package com.example.onlinekonobar;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TinyDB {
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public TinyDB(Context context) {
        sharedPreferences = context.getSharedPreferences("com.example.onlinekonobar", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void putListObject(String key, ArrayList<?> list) {
        String json = gson.toJson(list);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public <T> ArrayList<T> getListObject(String key, Class<T> clazz) {
        String json = sharedPreferences.getString(key, null);
        Type type = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return gson.fromJson(json, type);
    }
}
