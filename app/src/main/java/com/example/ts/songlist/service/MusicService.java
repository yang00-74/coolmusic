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
import com.example.ts.songlist.utils.LogUtil;
import com.example.ts.songlist.utils.MediaPlayerProxy;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class MusicService extends Service implements Serializable {

    public MediaPlayerProxy mMediaPlayerProxy = new MediaPlayerProxy();

    private int mCurrentPosition = 1;
    private Song mSong ;
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
                        .setContentTitle(song.getmSongName())
                        .setContentText(song.getmArtist())
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
            if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.STARTED) {
                mMediaPlayerProxy.pause();
            } else {
                mMediaPlayerProxy.start();
            }
        }

        public void seekTo(int progress) {
            if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.STARTED) {
                mMediaPlayerProxy.seekTo(progress);
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

        //注册接收歌曲信息广播,显示在通知中
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Music_information");
        mLocalReceiver = new LocalReceiver();

        mLocalBroadcastManager.registerReceiver(mLocalReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        LogUtil.d("Service服务开始", "1230asd");

        int musicId = 1;
        if (intent != null && intent.hasExtra("musicId")) {
            musicId = intent.getIntExtra("musicId", 1);
        }

        LogUtil.d("Service准备播放了", "1230asd musicid:" + musicId);

        if (mCurrentPosition == musicId) {
            if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.PAUSED) {
                mMediaPlayerProxy.start();
            } else if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.IDLE) {
                play();
            } else {
                return super.onStartCommand(intent, flags, startId);
            }
        } else {
            mCurrentPosition = musicId;
            mMediaPlayerProxy.stop();
            mMediaPlayerProxy.reset();
            play();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public void play() {

        LogUtil.d("Service开始播放", "1230asd 当前位置" + mCurrentPosition);

     //   mSongList = DataSupport.where(" id =? ","2").find(Song.class);


         mSong = DataSupport.find(Song.class,mCurrentPosition);
        //设置url音乐文件源
        String url = mSong.getmFileUrl();

        //广播发送歌曲信息
        Intent intent = new Intent("Music_information");
        intent.putExtra("music", mSong);
        mLocalBroadcastManager.sendBroadcast(intent);

        mMediaPlayerProxy.setDataSource(url);
        mMediaPlayerProxy.prepare();

        mMediaPlayerProxy.start();

        //实现自动下一首逻辑
        mMediaPlayerProxy.getMediaPlayer().setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        LogUtil.d("Service自动播放", "1230asd");
                        next();
                    }
                });
    }

    public void next() {
        if (mCurrentPosition < DataSupport.count(Song.class)) {
            mCurrentPosition += 1;

        } else {
            mCurrentPosition = 1;
        }
        mMediaPlayerProxy.stop();
        mMediaPlayerProxy.reset();
        play();
    }

    public void preview() {
        if (mCurrentPosition > 1) {
            mCurrentPosition -= 1;

        } else {
            mCurrentPosition = DataSupport.count(Song.class);
        }
        mMediaPlayerProxy.stop();
        mMediaPlayerProxy.reset();
        play();

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
                        if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.STARTED) {
                            mMusicListener.onProgressChange(mMediaPlayerProxy.getCurrentPosition(), mMediaPlayerProxy.getDuration());
                            mMusicListener.onMusicStart(mSong);
                        }
                    } else {

                        LogUtil.d("Service接口为空", "123asd");
                    }
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

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

        mMediaPlayerProxy.release();
        mIsServiceRunning = false;

        stopSelf();
        mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
        super.onDestroy();
    }
}
