package com.example.ts.songlist;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.service.MusicService;
import com.example.ts.songlist.utils.HttpUtil;
import com.example.ts.songlist.utils.LogUtil;
import com.example.ts.songlist.utils.MediaPlayerProxy;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MusicActivity extends BasicActivity implements View.OnClickListener, Serializable {

    private static final String ZERO = "0";

    private static final String COLON = ":";

    private static final String MUSICID = "musicId";

    private static final String STARTING = "播放中";

    private static final String PAUSED = "已暂停";

    private final int WHAT_UPDATE_PROGRESS = 0;

    private final int WHAT_START_SERVICE = 1;

    private final int WHAT_BUTTON_UPDATE = 2;

    private int mSeekToProgress = 0;

    private int mChangeProgress = 0;

    private MusicService.MusicBinder mMusicBinder;

    private Button start_btn;

    private SeekBar mMusicProgressSeekBar;

    public DrawerLayout mDrawerLayout;

    private TextView mSonProgress;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBinder = (MusicService.MusicBinder) service;

            mMusicBinder.getMusicService().setCurrentMusicListener(new MusicService.CurrentMusicListener() {
                @Override
                public void onProgressChange(final int progress, final int max) {
                    LogUtil.d("Activity开始赋值mProgress", "123asd");

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mMusicProgressSeekBar.setMax(max);
//                            mMusicProgressSeekBar.setProgress(progress);
//
//                        }
//                    });

                    //use Async Message to update UI
                    Message msg = Message.obtain(mHandler, WHAT_UPDATE_PROGRESS);
                    msg.arg1 = progress;
                    msg.arg2 = max;
                    msg.sendToTarget();
                }

                @Override
                public void onMusicStart(Song song) {

                    Message msg = Message.obtain(mHandler, WHAT_START_SERVICE);
                    msg.obj = song;
                    msg.sendToTarget();

                }

                @Override
                public void onMediaPlayStatus(MediaPlayerProxy.MediaPlayerStatus status) {
                    Message message = Message.obtain(mHandler, WHAT_BUTTON_UPDATE);
                    message.obj = status;
                    message.sendToTarget();
                }
            });

            mMusicBinder.getMusicService().getMusicInfo();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_UPDATE_PROGRESS:
                    displayMusicProgress(msg.arg2, msg.arg1);
                    break;

                case WHAT_START_SERVICE:
                    Song song = (Song) msg.obj;
                    initUI(song);
                    break;

                case WHAT_BUTTON_UPDATE:
                    if (msg.obj == MediaPlayerProxy.MediaPlayerStatus.STARTED) {
                        start_btn.setText(STARTING);
                    } else if (msg.obj == MediaPlayerProxy.MediaPlayerStatus.PAUSED) {
                        start_btn.setText(PAUSED);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        start_btn = findViewById(R.id.start_button);
        start_btn.setOnClickListener(this);

        bingPicImg = findViewById(R.id.bing_pic_img);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String bingPic = preferences.getString("bing_pic", null);

        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        //swipeRefreshLayout used to refresh the image
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBingPic();

            }
        });

        mSonProgress = findViewById(R.id.song_progress);

        Button preview_btn = findViewById(R.id.preview_button);
        preview_btn.setOnClickListener(this);

        Button next_btn = findViewById(R.id.next_button);
        next_btn.setOnClickListener(this);

        //滑动菜单
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mMusicProgressSeekBar = findViewById(R.id.mySeekBar1);
        mMusicProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    mSeekToProgress = progress;//设置变量记录进度，在完成的方法里实现音乐拖动，无卡顿
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMusicBinder.seekTo(mSeekToProgress);
            }
        });

        Intent intent = new Intent(this, MusicService.class);
        try {
            startService(intent);
            bindService(intent, connection, BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(MUSICID)) {
            int musicId = intent.getIntExtra(MUSICID, 1);
            try {
                toStartService(musicId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void toStartService(int musicId) {

        //send  song`s id to the service
        Intent start = new Intent(MusicActivity.this, MusicService.class);
        start.putExtra(MUSICID, musicId);
        startService(start);
    }


    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MusicActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MusicActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Glide.with(MusicActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    //display song`s name and total_time
    private void initUI(Song song) {
        if (null == song) {
            return;
        }
        //在TextView中显示歌曲时长
        TextView size = findViewById(R.id.size);

        Integer time = song.getmSize();
        Integer minute = time / 60000;
        String min = ZERO + minute;

        Integer seconds = time % 60000 / 1000;
        String sec;
        if (seconds < 10) {
            sec = ZERO + seconds;
        } else {
            sec = seconds.toString();
        }

        String musicLength = min + COLON + sec;

        size.setText(musicLength);

        //display the song`s name
        TextView songTitle = findViewById(R.id.song_title);
        songTitle.setText(song.getmSongName());

    }

    //display the music`s progress
    private void displayMusicProgress(int max, int progress) {

        mMusicProgressSeekBar.setMax(max);
        if (mChangeProgress != mSeekToProgress) {
            mChangeProgress = mSeekToProgress;
            mMusicProgressSeekBar.setProgress(mChangeProgress);
        } else {
            mMusicProgressSeekBar.setProgress(progress);
        }

        Integer minute = progress / 60000;
        String min = ZERO + minute;
        Integer seconds = progress % 60000 / 1000;
        String sec;
        if (seconds < 10) {
            sec = ZERO + seconds;
        } else {
            sec = seconds.toString();
        }
        String currentLength = min + COLON + sec;
        mSonProgress.setText(currentLength);
    }

    //There is button clicked event
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                mMusicBinder.start();

                break;

            case R.id.next_button:
                mMusicBinder.nextMusic();

                break;

            case R.id.preview_button:
                mMusicBinder.previewMusic();

                break;
            default:
                break;
        }
    }

    protected void onDestroy() {
        onUnbindService();
        super.onDestroy();
    }

    public void onUnbindService() {
        unbindService(connection);
    }
}
