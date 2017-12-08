package com.example.ts.songlist.utils;

import android.media.MediaPlayer;

import java.io.IOException;

public class MediaPlayerProxy {
    private MediaPlayer mMediaPlayer;
    private MediaPlayerStatus mStatus;

    private OnCompletionListener mOnCompletionListener;

    public MediaPlayerProxy() {
        mMediaPlayer = new MediaPlayer();
        mStatus = MediaPlayerStatus.IDLE;
    }

    public void reset() {
        mMediaPlayer.reset();
        mStatus = MediaPlayerStatus.IDLE;
    }

    public void pause(){
        mMediaPlayer.pause();
        mStatus=MediaPlayerStatus.PAUSED;
    }

    public void seekTo(int progress){
        mMediaPlayer.seekTo(progress);
    }

    public void setDataSource(String url){
        try {
            mMediaPlayer.setDataSource(url);
            mStatus=MediaPlayerStatus.INITIALIZED;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepare(){
        try {
            mMediaPlayer.prepare();
            mStatus=MediaPlayerStatus.PREPARED;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
            mMediaPlayer.start();
            mStatus = MediaPlayerStatus.STARTED;
    }

    public void stop(){
        mMediaPlayer.stop();
        mStatus=MediaPlayerStatus.STOPPED;
    }

    public void release(){
        mMediaPlayer.release();
        mStatus=MediaPlayerStatus.IDLE;
    }

    public MediaPlayerStatus getStatus() {
        return mStatus;
    }

    public enum MediaPlayerStatus {
        IDLE,
        INITIALIZED,
        PREPARED,
        STARTED,
        STOPPED,
        PAUSED,
    }

    public MediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }

    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mMediaPlayer.getDuration();
    }

    public interface OnCompletionListener extends MediaPlayer.OnCompletionListener{
        @Override
        void onCompletion(MediaPlayer mp);
    }
    public void  setOnCompletionListener(OnCompletionListener l){
        mOnCompletionListener=l;
    }

}
