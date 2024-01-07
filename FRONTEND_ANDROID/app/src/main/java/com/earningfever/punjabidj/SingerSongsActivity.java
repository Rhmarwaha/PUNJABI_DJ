package com.earningfever.punjabidj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingerSongsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SingerModel singerModel;

    //sqllite database
    private DatabaseHelper db;
    private ArrayList<Song> sqliteDataBaseSongs;

    //views
    private RecyclerView recyclerViewSingerSongsActivity;
    private SwipeRefreshLayout swipeRefreshSingerSongsActivity;
    private CustomPaginationAdapter customPaginationAdapter;


    private int currentPage = PaginationListener.PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 1;
    private boolean isLoading = false;
    int itemCount = 0;

    //http://testcs.ml/rohit_songs/scripts/singer_songs.php?singer_id=2&page_id=1
    private static String JSON_URL = "http://otavalabs.com/rohit_songs/scripts/singer_songs.php?singer_id=";

    //Add View
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_songs);

        getData();

        initViews();
        addAds();
    }

    private void initViews() {

        db = new DatabaseHelper(this);
        sqliteDataBaseSongs = db.getSingerSongs(singerModel.getId());

        recyclerViewSingerSongsActivity = findViewById(R.id.recyclerViewSingerSongsActivity);

        swipeRefreshSingerSongsActivity = findViewById(R.id.swipeRefreshSingerSongsActivity);
        swipeRefreshSingerSongsActivity.setOnRefreshListener(this);

        recyclerViewSingerSongsActivity.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewSingerSongsActivity.setLayoutManager(linearLayoutManager);
        customPaginationAdapter = new CustomPaginationAdapter(this,new ArrayList<Song>());
        recyclerViewSingerSongsActivity.setAdapter(customPaginationAdapter);

        //fetch json
        fetchJson();

        recyclerViewSingerSongsActivity.addOnScrollListener(new PaginationListener(linearLayoutManager) {
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
                    Toast.makeText(SingerSongsActivity.this, "We Upload More Songs Soon ....", Toast.LENGTH_SHORT).show();
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
        sqliteDataBaseSongs = db.getSingerSongs(singerModel.getId());
        onRefresh();
    }


    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PaginationListener.PAGE_START;
        isLastPage = false;
        customPaginationAdapter.clear();
        sqliteDataBaseSongs = db.getSingerSongs(singerModel.getId());
        fetchJson();
    }
    @Override
    protected void onDestroy() {
        // adds
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
    private void fetchJson() {
        final ArrayList<Song> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL + singerModel.getId() + "&page_id=" +currentPage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject responseBody = new JSONObject(response);
                    totalPage = responseBody.getInt("no_pages");
                    Toast.makeText(SingerSongsActivity.this, totalPage+"", Toast.LENGTH_SHORT).show();
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
                    swipeRefreshSingerSongsActivity.setRefreshing(false);
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
                            Toast.makeText(SingerSongsActivity.this, "We Upload More Songs Soon ....", Toast.LENGTH_SHORT).show();
                        }

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SingerSongsActivity.this, "Network Issue ", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void getData() {

        singerModel = (SingerModel) getIntent().getSerializableExtra("singer_details");
        setTitle(singerModel.getName());
    }

    private void addAds(){
        adView = new AdView(this, "2919723114738051_2919872264723136", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container_singer_songs_activity);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad


        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(SingerSongsActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                Toast.makeText(SingerSongsActivity.this, "load",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });


        // Request an ad
        adView.loadAd();
    }
}
