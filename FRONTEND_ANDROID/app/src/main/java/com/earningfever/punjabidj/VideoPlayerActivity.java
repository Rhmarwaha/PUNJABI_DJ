package com.earningfever.punjabidj;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private Song song;
    private int currentVideoId=-1;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerListener youTubePlayerListener;
    private YouTubePlayer youTubePlayerRecycler;
    private ArrayList<Song> dataInArrayList = new ArrayList<>();

    //for below recyclerView

    private DatabaseHelper db;
    private ArrayList<Song> sqliteDataBaseSongs;

    //views
    private RecyclerView recyclerViewVideoPlayerActivity;
    private SwipeRefreshLayout swipeRefreshVideoPlayerActivity;
    private CustomVideoPlayerPaginationAdapter customVideoPlayerPaginationAdapter;

    private Switch autoPlaySwitch;

    private int currentPage = PaginationListener.PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 1;
    private boolean isLoading = false;
    int itemCount = 0;

    //http://testcs.ml/rohit_songs/scripts/singer_songs.php?singer_id=2&page_id=1
    private static String JSON_URL = "http://otavalabs.com/rohit_songs/scripts/singer_songs.php?singer_id=";


    //Add View
    private AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        getData();
        setTitle(song.getSongName());
        initYouTubePlayer();
        addAds();
        loadInterstitialView();

        //init RecyclerView
        initRecyclerView();

    }

    private void initYouTubePlayer() {
        youTubePlayerListener = new YouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayerRecycler = youTubePlayer;
                youTubePlayer.loadVideo(song.getSongLinkId(),0);
                Toast.makeText(VideoPlayerActivity.this, "Wait For a second .....", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState playerState) {

                switch(playerState){
                    case ENDED:
                        Toast.makeText(VideoPlayerActivity.this, "ended", Toast.LENGTH_SHORT).show();
                        if(autoPlaySwitch.isChecked()){
                            currentVideoId++;
                            if (dataInArrayList.size() != currentVideoId){
                                youTubePlayer.loadVideo(dataInArrayList.get(currentVideoId).getSongLinkId(),0);
                                setTitle(dataInArrayList.get(currentVideoId).getSongName());
                            }else{
                                youTubePlayer.loadVideo(song.getSongLinkId(),0);
                                currentVideoId = -1;
                            }
                        }
                }

            }

            @Override
            public void onPlaybackQualityChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError playerError) {

            }

            @Override
            public void onCurrentSecond(YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoDuration(YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoLoadedFraction(YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(YouTubePlayer youTubePlayer, String s) {

            }

            @Override
            public void onApiChange(YouTubePlayer youTubePlayer) {

            }
        };
        youTubePlayerView.setEnableAutomaticInitialization(false);
        youTubePlayerView.initialize(youTubePlayerListener);
    }


    private void getData() {
        song = (Song) getIntent().getSerializableExtra("song");
    }


    private void initRecyclerView(){

        autoPlaySwitch = findViewById(R.id.autoPlaySwitch);

        db = new DatabaseHelper(this);


        sqliteDataBaseSongs = db.getSingerSongs(song.getSingerId());

        recyclerViewVideoPlayerActivity = findViewById(R.id.recyclerViewVideoPlayerActivity);

        swipeRefreshVideoPlayerActivity = findViewById(R.id.swipeRefreshVideoPlayerActivity);
        swipeRefreshVideoPlayerActivity.setOnRefreshListener(this);

        recyclerViewVideoPlayerActivity.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewVideoPlayerActivity.setLayoutManager(linearLayoutManager);
        customVideoPlayerPaginationAdapter = new CustomVideoPlayerPaginationAdapter(this,new ArrayList<Song>());
        recyclerViewVideoPlayerActivity.setAdapter(customVideoPlayerPaginationAdapter);

        //fetch json
        fetchJson();

        recyclerViewVideoPlayerActivity.addOnScrollListener(new PaginationListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                if(currentPage <=totalPage){
                    fetchJson();
                }else{
                    customVideoPlayerPaginationAdapter.removeLoading();
                    isLastPage = true;
                    isLoading = false;
                    Toast.makeText(VideoPlayerActivity.this, "We Upload More Songs Soon ....", Toast.LENGTH_SHORT).show();
                }
                if(totalPage< currentPage){
                    customVideoPlayerPaginationAdapter.addItems(sqliteDataBaseSongs);
                    dataInArrayList.addAll(sqliteDataBaseSongs);
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
        customVideoPlayerPaginationAdapter.clear();
        sqliteDataBaseSongs = db.getSingerSongs(song.getSingerId());
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

                    if (currentPage != PaginationListener.PAGE_START) customVideoPlayerPaginationAdapter.removeLoading();
                    if (items.size() > 0){
                        customVideoPlayerPaginationAdapter.addItems(items);
                        dataInArrayList.addAll(items);
                    }
                    swipeRefreshVideoPlayerActivity.setRefreshing(false);
                    if (currentPage <= totalPage) {
                        customVideoPlayerPaginationAdapter.addLoading();
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
                            customVideoPlayerPaginationAdapter.removeLoading();
                            isLastPage = true;
                            isLoading = false;
                            Toast.makeText(VideoPlayerActivity.this, "We Upload More Songs Soon ....", Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VideoPlayerActivity.this, "Network Issue ", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void onClickCalled(String videoId,int position) {
        // Call another acitivty here and pass some arguments to it.
        Toast.makeText(this, position+"", Toast.LENGTH_SHORT).show();
        youTubePlayerRecycler.loadVideo(videoId,0);
        currentVideoId = position;
        setTitle(dataInArrayList.get(currentVideoId).getSongName());
    }

    private void addAds(){
        adView = new AdView(this, "2919723114738051_2919876444722718", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container_video_player_activity);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad


        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(VideoPlayerActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                Toast.makeText(VideoPlayerActivity.this, "load",
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

    private void loadInterstitialView(){

        interstitialAd = new InterstitialAd(this, "2919723114738051_2919889118054784");
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
