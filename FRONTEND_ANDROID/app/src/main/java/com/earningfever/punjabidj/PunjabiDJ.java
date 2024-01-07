package com.earningfever.punjabidj;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;

public class PunjabiDJ extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AudienceNetworkAds.initialize(this  );
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
