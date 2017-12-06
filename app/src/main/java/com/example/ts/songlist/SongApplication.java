package com.example.ts.songlist;

import android.app.Application;

public class SongApplication extends Application {
    private SongsManager mSongsManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mSongsManager = new SongsManager();
        mSongsManager.init();
    }

    public SongsManager getSongsManager() {
        return mSongsManager;
    }
}
