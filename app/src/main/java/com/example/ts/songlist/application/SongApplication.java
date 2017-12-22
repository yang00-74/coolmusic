package com.example.ts.songlist.application;

import android.annotation.SuppressLint;
import android.content.Context;

import org.litepal.LitePalApplication;

public class SongApplication extends LitePalApplication {

    private static SongsManager mSongsManager;
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public SongApplication(){
        sContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSongsManager = new SongsManager();
        mSongsManager.init();
    }

    public static SongsManager getSongsManager() {
        return mSongsManager;
    }

    public static Context getContext() {
        return sContext;
    }
}
