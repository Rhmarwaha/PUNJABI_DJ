package com.earningfever.punjabidj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
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

public class SingersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private CustomSingerPaginationAdapter customSingerPaginationAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentPage = PaginationListener.PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 2;
    private boolean isLoading = false;
    int itemCount = 0;

    private static String JSON_URL = "http://otavalabs.com/rohit_songs/scripts/read_singer.php?page_id=";


    //Add View
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singers);

        setTitle("Singers");
        initViews();
        addAds();
    }

    private void initViews() {

        //recycler view
        recyclerView = findViewById(R.id.recyclerViewSingersActivity);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        customSingerPaginationAdapter = new CustomSingerPaginationAdapter(this,new ArrayList<SingerModel>());
        recyclerView.setAdapter(customSingerPaginationAdapter);

        //swipe fresh layout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshSingersActivity);
        swipeRefreshLayout.setOnRefreshListener(this);


        fetchJson();

        recyclerView.addOnScrollListener(new PaginationListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                fetchJson();
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
        onRefresh();
    }
    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PaginationListener.PAGE_START;
        isLastPage = false;
        customSingerPaginationAdapter.clear();
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

    private void fetchJson(){
        final ArrayList<SingerModel> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL + currentPage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    //Toast.makeText(MainActivity.this, currentPage+"", Toast.LENGTH_SHORT).show();
                    JSONObject responseBody = new JSONObject(response);
                    totalPage = responseBody.getInt("no_pages");
                    JSONObject responseData = responseBody.getJSONObject("data");
                    JSONArray jsonArray = responseData.getJSONArray("records");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        itemCount++;
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i) + "");
                        SingerModel data = new SingerModel(jsonObject.getInt("id"), jsonObject.getString("name"),jsonObject.getString("image"));
                        items.add(data);
                        Log.d("ROHIT",data.getId()+"");
                    }
                       Toast.makeText(SingersActivity.this, items.size()+"", Toast.LENGTH_SHORT).show();
                    if (currentPage != PaginationListener.PAGE_START) customSingerPaginationAdapter.removeLoading();
                    if (items.size() > 0){
                        customSingerPaginationAdapter.addItems(items);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    if (currentPage < totalPage) {
                        customSingerPaginationAdapter.addLoading();
                    } else {
                        isLastPage = true;
                    }
                    isLoading = false;
                    if (items.size() <= 6 ) {
                        isLoading = true;
                        currentPage++;
                        if(currentPage <=totalPage){
                            fetchJson();
                        }else{
                            customSingerPaginationAdapter.removeLoading();
                            isLastPage = true;
                            isLoading = false;
                            Toast.makeText(SingersActivity.this, "We Upload More Singers Soon ....", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SingersActivity.this, "Network Issue ", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void addAds(){
        adView = new AdView(this, "2919723114738051_2919841534726209", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container_singers_activity);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad


        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(SingersActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                Toast.makeText(SingersActivity.this, "load",
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
