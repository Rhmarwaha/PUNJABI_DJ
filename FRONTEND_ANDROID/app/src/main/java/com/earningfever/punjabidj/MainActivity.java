package com.earningfever.punjabidj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private CustomPaginationAdapter customPaginationAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<Song> sqliteDataArrayList;
    private DatabaseHelper databaseHelper;

    private FloatingActionButton searchFloatingButton,singerFloatingButton;
    private TextView marqueeText;

    private int currentPage = PaginationListener.PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 2;
    private boolean isLoading = false;
    int itemCount = 0;

    private static String JSON_URL = "http://otavalabs.com/rohit_songs1/scripts/read.php?page_id=";


    //Add View
    private AdView adView;
    private InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        addAds();
        loadInterstitialView();
        floatingButtonListeners();
    }

    private void initViews() {

        searchFloatingButton = findViewById(R.id.searchFloatingButton);
        singerFloatingButton = findViewById(R.id.singerFloatingButton);

        marqueeText = findViewById(R.id.marqueeText);
        marqueeText.setSelected(true);

        //recycler view
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        customPaginationAdapter = new CustomPaginationAdapter(this,new ArrayList<Song>());
        recyclerView.setAdapter(customPaginationAdapter);

        //swipe fresh layout
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        //Sql management
        databaseHelper = new DatabaseHelper(this);
        sqliteDataArrayList = databaseHelper.getAllSongs();


        //fetch json
        fetchJson();

        recyclerView.addOnScrollListener(new PaginationListener(linearLayoutManager) {
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

    private void addAds(){
        adView = new AdView(this, "2919723114738051_2919728994737463", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container_main_activity);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad


        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                Toast.makeText(MainActivity.this, "load",
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

    private void floatingButtonListeners(){
        searchFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Serach Button", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,SearchingActivity.class);
                startActivity(intent);
            }
        });

        singerFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Singer Button", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,SingersActivity.class);
                startActivity(intent);

            }
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        sqliteDataArrayList = databaseHelper.getAllSongs();
        onRefresh();
    }

    @Override
    protected void onDestroy() {
        // adds
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PaginationListener.PAGE_START;
        isLastPage = false;
        customPaginationAdapter.clear();
        sqliteDataArrayList = databaseHelper.getAllSongs();
        fetchJson();
    }
    private void fetchJson(){
        final ArrayList<Song> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL + currentPage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject responseBody = new JSONObject(response);
                    totalPage = responseBody.getInt("no_pages");
                    JSONObject responseData = responseBody.getJSONObject("data");
                    JSONArray jsonArray = responseData.getJSONArray("records");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        itemCount++;
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i) + "");
                        Song data = new Song(jsonObject.getInt("id"), jsonObject.getString("name"),jsonObject.getString("image"),jsonObject.getString("song_link"), false,jsonObject.getInt("singer_id"));
                        items.add(data);
                    }
                    items.removeAll(sqliteDataArrayList);

                    if (currentPage != PaginationListener.PAGE_START) customPaginationAdapter.removeLoading();
                    if (items.size() > 0){
                        customPaginationAdapter.addItems(items);
                    }
                    swipeRefreshLayout.setRefreshing(false);
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
                            Toast.makeText(MainActivity.this, "We Upload More Songs Soon ....", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Network Issue ", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.shareAppButton:
                Toast.makeText(this, "share app selected", Toast.LENGTH_SHORT).show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                return true;

            case R.id.favourites:
                Toast.makeText(this, "Favourites", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,FavoriteActivity.class);
                startActivity(intent);
                return true;

            case R.id.instagramPage:
                Uri uri = Uri.parse("http://instagram.com/motivation__fever");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/motivation__fever")));
                }
                return true;

            case R.id.facebookPage:
                try {
                    this.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/104901217737263")));
                } catch (ActivityNotFoundException | PackageManager.NameNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/motivationfev")));
                }
                return true;

            case R.id.rateus:
                try {
                    Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }catch(ActivityNotFoundException e) {
                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }
                return true;

            case R.id.request:
                Intent requestIntent = new Intent(MainActivity.this, RequestSongActivity.class);
                startActivity(requestIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void loadInterstitialView(){

        interstitialAd = new InterstitialAd(this, "2919723114738051_2919913874718975");
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e("ROHIT", "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e("ROHIT", "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e("ROHIT", "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d("ROHIT", "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d("ROHIT", "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d("ROHIT", "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }

    @Override
    public void onBackPressed() {
        if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
            super.onBackPressed();
            return;
        }
        // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
        if(interstitialAd.isAdInvalidated()) {
            super.onBackPressed();
            return;
        }
        // Show the ad
        interstitialAd.show();
        super.onBackPressed();
    }
}
