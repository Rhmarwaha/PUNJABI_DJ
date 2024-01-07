package com.earningfever.punjabidj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchingSong extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private String JSON_URL_SEARCH_SONG = "http://otavalabs.com/rohit_songs/scripts/search_song_name.php?song_name_search='";

    private ImageView song_thumbnail_search_song;
    private TextView song_name_search_song;
    private Button song_favorite_button_search_song;
    private CardView cardViewLayout;
    private Song song;
    private String songName;

    private DatabaseHelper db;
    private ArrayList<Song> sqliteDataBaseSongs;

    //views
    private RecyclerView recyclerViewSearchingSongActivity;
    private SwipeRefreshLayout swipeRefreshSearchingSongActivity;
    private CustomPaginationAdapter customPaginationAdapter;


    private int currentPage = PaginationListener.PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 1;
    private boolean isLoading = false;
    int itemCount = 0;

    //http://testcs.ml/rohit_songs/scripts/singer_songs.php?singer_id=2&page_id=1
    private static String JSON_URL = "http://otavalabs.com/rohit_songs/scripts/singer_songs.php?singer_id=";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_song);

        initViews();
        getData();

        //initViews2();
    }

    private void getData() {
        song = (Song) getIntent().getSerializableExtra("searched_song");
        songName = getIntent().getStringExtra("song_name");
        setTitle(songName);
        if (song == null){
            fetchSongFromServer(songName);
        }else{
            song_name_search_song.setText(song.getSongName());
            Picasso.get().load(song.getSongImageLink()).placeholder(R.drawable.image_error).error(R.drawable.image_error).fit().into(song_thumbnail_search_song);
            song_favorite_button_search_song.setBackgroundResource(song.isFavorite()? R.drawable.favorite:R.drawable.favorite_border );
            initViews2();
        }

    }

    private void initViews(){

        song_thumbnail_search_song = findViewById(R.id.song_thumbnail_search_song);
        song_name_search_song = findViewById(R.id.song_name_search_song);
        song_favorite_button_search_song = findViewById(R.id.song_favorite_button_search_song);
        cardViewLayout = findViewById(R.id.cardViewLayout);

        db = new DatabaseHelper(this);

        song_favorite_button_search_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                song_favorite_button_search_song.setBackgroundResource(!song.isFavorite()? R.drawable.favorite:R.drawable.favorite_border );
                if(song.isFavorite()){
                    db.deleteSong(song);
                    Toast.makeText(SearchingSong.this, "Removed From Favourites", Toast.LENGTH_SHORT).show();
                    song.setFavorite(!song.isFavorite());
                    return;
                }
                db.insertSong(song);
                Toast.makeText(SearchingSong.this, "Added To Favourites", Toast.LENGTH_SHORT).show();
                song.setFavorite(!song.isFavorite());
            }
        });

        cardViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchingSong.this,VideoPlayerActivity.class);
                intent.putExtra("song",  song);
                startActivity(intent);
            }
        });
    }
    private void fetchSongFromServer(String songName){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL_SEARCH_SONG + songName + "'", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    //Toast.makeText(MainActivity.this, currentPage+"", Toast.LENGTH_SHORT).show();
                    JSONObject responseBody = new JSONObject(response);
                    JSONArray jsonArray = responseBody.getJSONArray("records");

                    JSONObject jsonObject = new JSONObject(jsonArray.get(0) + "");
                    song = new Song(jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("image"), jsonObject.getString("song_link"), false,jsonObject.getInt("singer_id"));
                    song_name_search_song.setText(song.getSongName());
                    Picasso.get().load(song.getSongImageLink()).placeholder(R.drawable.image_error).error(R.drawable.image_error).fit().into(song_thumbnail_search_song);
                    song_favorite_button_search_song.setBackgroundResource(song.isFavorite()? R.drawable.favorite:R.drawable.favorite_border );

                    initViews2();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchingSong.this, "Network Issue ", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void initViews2(){


        sqliteDataBaseSongs = db.getSingerSongs(song.getSingerId());

        recyclerViewSearchingSongActivity = findViewById(R.id.recyclerViewSearchingSongActivity);

        swipeRefreshSearchingSongActivity = findViewById(R.id.swipeRefreshSearchingSongActivity);
        swipeRefreshSearchingSongActivity.setOnRefreshListener(this);

        recyclerViewSearchingSongActivity.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewSearchingSongActivity.setLayoutManager(linearLayoutManager);
        customPaginationAdapter = new CustomPaginationAdapter(this,new ArrayList<Song>());
        recyclerViewSearchingSongActivity.setAdapter(customPaginationAdapter);

        //fetch json
        fetchJson();

        recyclerViewSearchingSongActivity.addOnScrollListener(new PaginationListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                if(currentPage <=totalPage){
                    fetchJson();
                }else{
                    customPaginationAdapter.removeLoading();
                    isLastPage = true;
                    isLoading = false;
                    Toast.makeText(SearchingSong.this, "We Upload More Songs Soon ....", Toast.LENGTH_SHORT).show();
                }
                if(totalPage< currentPage){
                    customPaginationAdapter.addItems(sqliteDataBaseSongs);
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        sqliteDataBaseSongs = db.getSingerSongs(song.getSingerId());
        onRefresh();
    }


    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PaginationListener.PAGE_START;
        isLastPage = false;
        customPaginationAdapter.clear();
        sqliteDataBaseSongs = db.getSingerSongs(song.getSingerId());
        fetchJson();
    }
    private void fetchJson() {
        final ArrayList<Song> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL + song.getSingerId() + "&page_id=" +currentPage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject responseBody = new JSONObject(response);
                    totalPage = responseBody.getInt("no_pages");
                   // Toast.makeText(SearchingSong.this, totalPage+"", Toast.LENGTH_SHORT).show();
                    JSONObject responseData = responseBody.getJSONObject("data");
                    JSONArray jsonArray = responseData.getJSONArray("records");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        itemCount++;
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i) + "");
                        Song data = new Song(jsonObject.getInt("id"), jsonObject.getString("name"),jsonObject.getString("image"),jsonObject.getString("song_link"), false,jsonObject.getInt("singer_id"));
                        items.add(data);
                    }
                    items.removeAll(sqliteDataBaseSongs);

                    if (currentPage != PaginationListener.PAGE_START) customPaginationAdapter.removeLoading();
                    if (items.size() > 0){
                        customPaginationAdapter.addItems(items);
                    }
                    swipeRefreshSearchingSongActivity.setRefreshing(false);
                    if (currentPage <= totalPage) {
                        customPaginationAdapter.addLoading();
                    } else {
                        isLastPage = true;
                    }
                    isLoading = false;
                    if (items.size() <= 5 ) {
                        isLoading = true;
                        currentPage++;
                        if(currentPage <=totalPage){
                            fetchJson();
                        }else{
                            customPaginationAdapter.removeLoading();
                            isLastPage = true;
                            isLoading = false;
                            Toast.makeText(SearchingSong.this, "We Upload More Songs Soon ....", Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchingSong.this, "Network Issue ", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


}
