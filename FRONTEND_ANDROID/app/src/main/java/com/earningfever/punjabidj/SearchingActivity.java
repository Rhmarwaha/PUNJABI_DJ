package com.earningfever.punjabidj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchingActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private String JSON_URL = "http://otavalabs.com/rohit_songs/scripts/search.php";
    private ArrayList<String> songsName = new ArrayList<>();
    private ArrayList<String> songsItems;

    private ListView listViewSearching;
    private SearchListViewAdapter adapter;
    private SearchView editsearch;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        initViews();
        clickListeners();
    }


    private void initViews() {
        setTitle("Search Songs");
        listViewSearching = findViewById(R.id.listViewSearching);

        fetchJson();
        adapter = new SearchListViewAdapter(this, songsName);
        listViewSearching.setAdapter(adapter);

        editsearch = findViewById(R.id.searchView);
        editsearch.setOnQueryTextListener(this);
        editsearch.onActionViewExpanded();

        db = new DatabaseHelper(this);
    }

    private void clickListeners() {
        listViewSearching.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String)adapter.getItem(position);
                editsearch.setQuery(songName,true);
                fetchSongFromDataBase(songName);

            }
        });
    }

    private void fetchSongFromDataBase(String songName) {
        Song song = db.getSong(songName);
        Intent intent = new Intent(SearchingActivity.this,SearchingSong.class);
        intent.putExtra("searched_song",song);
        intent.putExtra("song_name",songName);
        startActivity(intent);
    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }

    private void fetchJson() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseBody = new JSONObject(response);
                    JSONArray jsonArray = responseBody.getJSONArray("records");

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    songsItems = gson.fromJson(jsonArray.toString(), type);
                    adapter.addItems(songsItems);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchingActivity.this, "Network Issue ", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
