package com.mkandeel.socialmediaauthentication;

import android.app.Application;

import com.parse.Parse;
import com.parse.twitter.ParseTwitterUtils;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.app_id))
                .clientKey(getResources().getString(R.string.client_key))
                .server(getResources().getString(R.string.back4app_server_url))
                .enableLocalDataStore() // <--- ADD THIS HERE!
                .build()
        );
        ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
    }
}
