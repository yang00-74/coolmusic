package com.example.ts.songlist.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.ts.songlist.MusicActivity;
import com.example.ts.songlist.R;
import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.utils.AudioUtil;
import com.example.ts.songlist.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {

    public MediaPlayer mediaPlayer = new MediaPlayer();

    private int currentPosition;
    private ArrayList<Song> mSongList;
    private MusicBinder mBinder = new MusicBinder();


    public class MusicBinder extends Binder {

        public MusicService getMusicService() {
            return MusicService.this;
        }

        public void nextMusic() {
            next();
        }

        public void previewMusic() {
            preview();
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
        LogUtil.d("Myservice", "服务创建了");
        mSongList = AudioUtil.getAllSongs(this.getContentResolver());

        //前台服务
        Intent intent = new Intent(this, MusicActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification mNotification = new NotificationCompat.Builder(this)
                .setContentTitle("这是一个标题")
                .setContentText("这是一首歌")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .build();
        startForeground(1, mNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("Myservice", "服务启动了");

        int position = intent.getIntExtra("position", 0);
        if (currentPosition == position) {
            return super.onStartCommand(intent, flags, startId);
        }
        currentPosition = position;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        //使用多线程处理服务逻辑
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开始服务的逻辑
                play();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.d("Myservice", "服务销毁了");
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }

    public void play() {
        Song song = mSongList.get(currentPosition);
        //设置url音乐文件源
        String url = song.getFileUrl();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        //实现自动下一首逻辑
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
//                if (currentPosition < mSongList.size() - 1) {
//                    currentPosition += 1;
//                    mediaPlayer.reset();
//                    //开始服务的逻辑
//                    Song song = mSongList.get(currentPosition);
//                    //设置url音乐文件源
//                    String url = song.getFileUrl();
//                    try {
//                        mediaPlayer.setDataSource(url);
//                        mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mediaPlayer.start();
//                } else {
//                    //自行终止服务
//                    stopSelf();
//                }

            }
        });

    }

    public void next() {
        if (currentPosition < mSongList.size() - 1) {
            currentPosition += 1;
            mediaPlayer.reset();
            play();
        } else {
            currentPosition = 0;
            mediaPlayer.reset();
            play();
        }
    }

    public void preview() {
        if (currentPosition > 0) {
            currentPosition -= 1;
            mediaPlayer.reset();
            play();
        } else {
            currentPosition = mSongList.size() - 1;
            mediaPlayer.reset();
            play();
        }
    }

}
