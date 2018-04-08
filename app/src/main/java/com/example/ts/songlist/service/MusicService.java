package com.example.ts.songlist.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.ts.songlist.MusicActivity;
import com.example.ts.songlist.R;
import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.utils.ActivityCollector;
import com.example.ts.songlist.utils.LogUtil;
import com.example.ts.songlist.utils.MediaPlayerProxy;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class MusicService extends Service implements Serializable {

    private final int WHAT_UPDATE_NOTIFICATION = 0;

    public MediaPlayerProxy mMediaPlayerProxy = new MediaPlayerProxy();

    private int mCurrentPosition = 1;

    private static final String ZERO = "0";

    private static final String COLON = ":";

    private static final String MUSICID = "musicId";

    private static final String PREVIEW = "preview";

    private static final String NEXT = "next";

    private static final String EXIT = "exit";

    private static final String PAUSE_OR_PLAY = "pause_or_play";

    private Song mSong;

    private MusicBinder mBinder = new MusicBinder();

    private CurrentMusicListener mMusicListener;

    private LocalReceiver mLocalReceiver;

    private boolean mIsServiceRunning = true;

    private NotificationManager mManager;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_UPDATE_NOTIFICATION:
                    setNotification(mSong);
                    break;
                default:
                    break;

            }
        }
    };

    class LocalReceiver extends BroadcastReceiver implements Serializable {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {

            if (PREVIEW.equals(intent.getAction())) {

                preview();

            } else if (NEXT.equals(intent.getAction())) {

                next();

            } else if (PAUSE_OR_PLAY.equals(intent.getAction())) {

                if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.STARTED) {

                    mMediaPlayerProxy.pause();
                } else {

                    mMediaPlayerProxy.start();
                }

            } else if (EXIT.equals(intent.getAction())) {

                mIsServiceRunning = false;

                ActivityCollector.finishAll();
                stopSelf();
                mManager.cancel(100);
                //stop service and finish the all activities
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //register the broadcastReceiver to receive the action
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PAUSE_OR_PLAY);
        intentFilter.addAction(PREVIEW);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(EXIT);

        mLocalReceiver = new LocalReceiver();
        registerReceiver(mLocalReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        LogUtil.d("Service服务开始", "1230asd");

        if (intent != null && intent.hasExtra(MUSICID)) {
            int musicId = intent.getIntExtra(MUSICID, 1);
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
            onUpdateNotification();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    public void play() {

        LogUtil.d("Service开始播放", "1230asd 当前位置" + mCurrentPosition);

        mSong = DataSupport.find(Song.class, mCurrentPosition);

        String url = mSong.getmFileUrl();

        if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.STARTED) {
            mMediaPlayerProxy.stop();
        }
        if (mMediaPlayerProxy.getStatus() != MediaPlayerProxy.MediaPlayerStatus.IDLE) {
            mMediaPlayerProxy.reset();
        }
        mMediaPlayerProxy.setDataSource(url);
        mMediaPlayerProxy.prepare();
        //实现自动下一首逻辑
        mMediaPlayerProxy.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.d("1230asd", "下一首了");
                next();
            }
        });

        mMediaPlayerProxy.getMediaPlayer().setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.d("1230asd", "出错了");
                play();
                return true;
            }
        });

        mMediaPlayerProxy.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setNotification(Song song) {
        if (null == song) {
            return;
        }

        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.notice_layout);
        mRemoteViews.setTextViewText(R.id.song_artist, song.getmArtist());
        mRemoteViews.setTextViewText(R.id.song_title, song.getmSongName());

        if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.STARTED) {
            int progress = mMediaPlayerProxy.getCurrentPosition();

            mRemoteViews.setTextViewText(R.id.song_progress, displayCurrentProgress(progress));
            mRemoteViews.setProgressBar(R.id.notice_progressBar, song.getmSize(),
                    progress, false);
        }

        if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.STARTED) {
            mRemoteViews.setImageViewResource(R.id.bt_play, R.mipmap.music_play);
        } else {
            mRemoteViews.setImageViewResource(R.id.bt_play, R.mipmap.music_pause);
        }

        //click UI turn to MusicActivity
        Intent intent = new Intent(this, MusicActivity.class);
        intent.putExtra(MUSICID, song.getId());
        PendingIntent pi_go = PendingIntent.getActivity(this, 5,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notice, pi_go);

        //click to exit the service
        Intent exit = new Intent(EXIT);
        PendingIntent pi_exit = PendingIntent.getBroadcast(this, 0,
                exit, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_exit, pi_exit);

        //set play preview song
        Intent pre = new Intent(PREVIEW);
        PendingIntent pi_pre = PendingIntent.getBroadcast(this, 1,
                pre, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_prev, pi_pre);

        //set play or pause
        Intent pause_or_play = new Intent(PAUSE_OR_PLAY);
        PendingIntent pi_pause_play = PendingIntent.getBroadcast(this, 2,
                pause_or_play, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_play, pi_pause_play);

        //set play next song
        Intent next = new Intent(NEXT);
        PendingIntent pi_next = PendingIntent.getBroadcast(this, 3,
                next, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_next, pi_next);

        //set the notification values
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();
        notification.contentView = mRemoteViews;
        notification.bigContentView = mRemoteViews;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        // startForeground(100,notification);
        mManager.notify(100, notification);
    }

    /**
     * used to display the song`s progress in the notification
     */
    public String displayCurrentProgress(int progress) {
        Integer minute = progress / 60000;
        String min = ZERO + minute;
        Integer seconds = progress % 60000 / 1000;
        String sec;
        if (seconds < 10) {
            sec = ZERO + seconds;
        } else {
            sec = seconds.toString();
        }

        return min + COLON + sec;
    }

    public void next() {
        if (mCurrentPosition < DataSupport.count(Song.class)) {
            mCurrentPosition += 1;

        } else {
            mCurrentPosition = 1;
        }
        mMediaPlayerProxy.stop();
        play();
    }

    public void preview() {
        if (mCurrentPosition > 1) {
            mCurrentPosition -= 1;

        } else {
            mCurrentPosition = DataSupport.count(Song.class);
        }
        mMediaPlayerProxy.stop();
        play();

    }

    public void getMusicInfo() {

        //开线程回调歌曲进程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsServiceRunning) {
                    if (mMusicListener != null) {

                        if (mMediaPlayerProxy.getStatus() == MediaPlayerProxy.MediaPlayerStatus.STARTED) {

                            mMusicListener.onProgressChange(mMediaPlayerProxy.getCurrentPosition(), mMediaPlayerProxy.getDuration());
                            mMusicListener.onMusicStart(mSong);
                        }
                    }
                    mMusicListener.onMediaPlayStatus(mMediaPlayerProxy.getStatus());
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    public void onUpdateNotification() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsServiceRunning) {

                    mHandler.removeMessages(WHAT_UPDATE_NOTIFICATION);
                    Message msg = Message.obtain(mHandler, WHAT_UPDATE_NOTIFICATION);
                    msg.sendToTarget();

                    Intent intent = new Intent(MUSICID);
                    intent.putExtra(MUSICID, mCurrentPosition - 1);
                    sendBroadcast(intent);

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
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

        void onMediaPlayStatus(MediaPlayerProxy.MediaPlayerStatus status);

        void onProgressChange(int progress, int max);
    }

    @Override
    public void onDestroy() {

        mMediaPlayerProxy.release();
        mIsServiceRunning = false;

        unregisterReceiver(mLocalReceiver);
        super.onDestroy();
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
                mMediaPlayerProxy.seekTo(progress);
        }

    }

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
