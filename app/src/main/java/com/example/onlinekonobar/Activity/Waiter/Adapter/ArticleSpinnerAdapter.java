package com.example.onlinekonobar.Activity.Waiter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.onlinekonobar.Api.Article;

import java.util.List;

public class ArticleSpinnerAdapter extends ArrayAdapter<Article> {

    public ArticleSpinnerAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_spinner_item, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        if (article != null) {
            textView.setText(article.getNaziv());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
