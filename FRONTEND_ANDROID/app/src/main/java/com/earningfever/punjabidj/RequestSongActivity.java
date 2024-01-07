package com.earningfever.punjabidj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

public class RequestSongActivity extends AppCompatActivity {

    private EditText nameRequestSong, descriptionRequestSeong;
    private Button requestSongButton;

    //http://testcs.ml/rohit_songs/scripts/get_request.php?name=%22Rohit%22&request=%22qwerty%22
    private String REQUEST_URL = "http://otavalabs.com/rohit_songs/scripts/get_request.php?name=\"";

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_song);

        setTitle("Request Song");
        nameRequestSong = findViewById(R.id.nameRequestSong);
        descriptionRequestSeong = findViewById(R.id.descriptionRequestSong);
        requestSongButton = findViewById(R.id.requestSongButton);

        addAds();

        requestSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameText = nameRequestSong.getText().toString();
                String descriptionString = descriptionRequestSeong.getText().toString();

                if(nameText.isEmpty()){
                    Toast.makeText(RequestSongActivity.this, "Please enter a Valid Name", Toast.LENGTH_SHORT).show();
                    return;
                }else if(descriptionString.isEmpty()){
                    Toast.makeText(RequestSongActivity.this, "Please enter a valid description", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    makeRequest(nameText,descriptionString);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        // adds
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void makeRequest(String nameText, String descriptionString) {
        String CREATE_URL = REQUEST_URL+nameText+"\"&request=\""+descriptionString+"\"";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,  CREATE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseBody = new JSONObject(response);
                    int responseCode = responseBody.getInt("response_code");
                    String message = responseBody.getString("message");

                    if(responseCode == 200 && message.equals("ok")){
                        Toast.makeText(RequestSongActivity.this, "Your request  sent..", Toast.LENGTH_SHORT).show();
                        nameRequestSong.setText("");
                        descriptionRequestSeong.setText("");
                    }else{
                        Toast.makeText(RequestSongActivity.this, "Please don't use symbols Try again....", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RequestSongActivity.this, "Network Issue ", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
    private void addAds(){
        adView = new AdView(this, "2919723114738051_2919774131399616", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container_request_song);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad


        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(RequestSongActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                Toast.makeText(RequestSongActivity.this, "load",
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
