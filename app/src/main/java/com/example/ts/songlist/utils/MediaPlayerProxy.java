package com.example.ts.songlist.utils;

import android.media.MediaPlayer;

public class MediaPlayerProxy {
    private MediaPlayer mMediaPlayer;
    private MediaPlayerStatus mStatus;

    public MediaPlayerProxy() {
        mMediaPlayer = new MediaPlayer();
        mStatus = MediaPlayerStatus.IDLE;
    }

    public void reset() {
        mMediaPlayer.reset();
        mStatus = MediaPlayerStatus.IDLE;
    }

    public void start() {
        if (mStatus != MediaPlayerStatus.PREPARED) {
            // TODO: 17-11-30
        } else {
            mMediaPlayer.start();
            mStatus = MediaPlayerStatus.STARTED;
        }
    }

    public MediaPlayerStatus getStatus() {
        return mStatus;
    }

    public boolean can(MediaPlayerAction action) {
        switch (action) {
            case START:
                return mStatus == MediaPlayerStatus.PREPARED;
        }
        return true;
    }

    public enum MediaPlayerStatus {
        IDLE,
        INITIALIZED,
        PREPARED,
        STARTED,
        STOPPED,
        PAUSED,
    }

    public enum MediaPlayerAction {
        START,
    }
}
