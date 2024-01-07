package com.earningfever.punjabidj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private CustomFavoriteAdapter customFavoriteAdapter;
    private ArrayList<Song> sqliteDate;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initViews();
        getSqliteData();
    }

    private void initViews() {
        setTitle("Favourites");
        db = new DatabaseHelper(this);
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        favoritesRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void getSqliteData(){
        if (sqliteDate != null){
            sqliteDate.clear();
        }

        sqliteDate = db.getAllSongs();
        customFavoriteAdapter = new CustomFavoriteAdapter(this,sqliteDate);
        favoritesRecyclerView.setAdapter(customFavoriteAdapter);
    }
}
