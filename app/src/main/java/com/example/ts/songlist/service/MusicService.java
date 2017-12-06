package com.example.ts.songlist.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.ts.songlist.MusicActivity;
import com.example.ts.songlist.R;
import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.utils.AudioUtil;
import com.example.ts.songlist.utils.LogUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class MusicService extends Service implements Serializable {

    public MediaPlayer mMediaPlayer = new MediaPlayer();

    private int mCurrentPosition;
    private ArrayList<Song> mSongList;
    private MusicBinder mBinder = new MusicBinder();

    private CurrentMusicListener mMusicListener;

    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    private boolean mIsServiceRunning = true;

    class LocalReceiver extends BroadcastReceiver implements Serializable {

        @Override
        public void onReceive(Context context, Intent intent) {
            //前台服务,获取歌曲信息显示在通知中
            if ("Music_information".equals(intent.getAction())) {
                Song song = (Song) intent.getSerializableExtra("music");
                Intent intent2 = new Intent(MusicService.this, MusicActivity.class);
                //延迟的意图
                PendingIntent pi = PendingIntent.getActivity(MusicService.this, 0, intent2, 0);

                Notification notification = new NotificationCompat.Builder(MusicService.this)
                        .setContentTitle(song.getSongName())
                        .setContentText(song.getArtist())
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pi)//设置延迟意图
                        .setAutoCancel(true)//设置点击后自动消失
                        .build();
                startForeground(1, notification);
            }
        }
    }


    public class MusicBinder extends Binder implements Serializable {
        public MusicService getMusicService() {
            return MusicService.this;
        }

        public void nextMusic() {
            next();
        }

        public void previewMusic() {
            preview();
        }

        public void start() {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                mMediaPlayer.start();
            }
        }

        public void seekTo(int progress) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.seekTo(progress);
            }
        }

    }

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("Service服务创建", "123asd");

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        mSongList = AudioUtil.getAllSongs(this.getContentResolver());
        //注册接收歌曲信息广播,显示在通知中
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Music_information");
        mLocalReceiver = new LocalReceiver();

        mLocalBroadcastManager.registerReceiver(mLocalReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        LogUtil.d("Service服务开始", "123asd");

        int position = 0;
        if (intent != null && intent.hasExtra("currentPosition")) {
            position = intent.getIntExtra("currentPosition", 0);
        }

        LogUtil.d("Service准备播放了", "123asd");

        if (mCurrentPosition == position) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            } else {
                return super.onStartCommand(intent, flags, startId);
            }
        } else {
            mCurrentPosition = position;
            mMediaPlayer.reset();
            play();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void getMusicInfo() {

        LogUtil.d("Service开始回调接口", "123asd");
        //开线程回调歌曲进程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsServiceRunning) {
                    if (mMusicListener != null) {

                        LogUtil.d("Service接口不空", "123asd开始设置数据");
                        if (mMediaPlayer.isPlaying()) {
                            mMusicListener.onProgressChange(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
                            mMusicListener.onMusicStart(mSongList.get(mCurrentPosition));
                        }
                    } else {
                        LogUtil.d("Service接口为空", "123asd");
                    }

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    public void play() {

        LogUtil.d("Service开始播放", "123asd");

        Song song = mSongList.get(mCurrentPosition);
        //设置url音乐文件源
        String url = song.getFileUrl();

        //广播发送歌曲信息
        Intent intent = new Intent("Music_information");
        intent.putExtra("music", song);
        mLocalBroadcastManager.sendBroadcast(intent);

        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();

        //实现自动下一首逻辑
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }

    public void next() {
        if (mCurrentPosition < mSongList.size() - 1) {
            mCurrentPosition += 1;
            mMediaPlayer.reset();
            play();
        } else {
            mCurrentPosition = 0;
            mMediaPlayer.reset();
            play();
        }
    }

    public void preview() {
        if (mCurrentPosition > 0) {
            mCurrentPosition -= 1;
            mMediaPlayer.reset();
            play();
        } else {
            mCurrentPosition = mSongList.size() - 1;
            mMediaPlayer.reset();
            play();
        }
    }

    public void setCurrentMusicListener(CurrentMusicListener currentMusicListener) {

        LogUtil.d("Service设置了接口", "123asd");
        mMusicListener = currentMusicListener;
    }

    public interface CurrentMusicListener {
        void onMusicStart(Song song);

        void onProgressChange(int progress, int max);
    }

    @Override
    public void onDestroy() {

        mMediaPlayer.release();
        mIsServiceRunning = false;
        stopSelf();
        mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
        super.onDestroy();
    }
}
