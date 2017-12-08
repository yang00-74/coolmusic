package com.example.ts.songlist;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.service.MusicService;
import com.example.ts.songlist.utils.LogUtil;

import java.io.Serializable;

public class MusicActivity extends BasicActivity implements View.OnClickListener, Serializable {
    private final int WHAT_UPDATE_PROGRESS = 0;
    private final int WHAT_START_SERVICE = 1;

    private MusicService.MusicBinder mMusicBinder;
    private SeekBar mMusicProgressSeekBar;
    public DrawerLayout mDrawerLayout;
    private Song mSong;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBinder = (MusicService.MusicBinder) service;

            LogUtil.d("Activity绑定上了", "123asd");

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

                    //异步消息机制实现更新UI
                    Message msg = Message.obtain(mHandler, WHAT_UPDATE_PROGRESS);
                    msg.arg1 = progress;
                    msg.arg2 = max;
                    msg.sendToTarget();
                }

                @Override
                public void onMusicStart(Song song) {
                    LogUtil.d("Activity开始赋值mSong", "123asd");

                    Message msg = Message.obtain(mHandler, WHAT_START_SERVICE);
                    msg.obj = song;
                    msg.sendToTarget();

                    LogUtil.d("Activity初始化UI", "123asd");
                }
            });

            LogUtil.d("Activity调用获取接口数据方法", "123asd");
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
                    mMusicProgressSeekBar.setMax(msg.arg2);
                    mMusicProgressSeekBar.setProgress(msg.arg1);
                    break;

                case WHAT_START_SERVICE:
                    mSong = (Song) msg.obj;
                    initUI();
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


        //    ((SongApplication)getApplication()).getSongsManager()

        //获取intent对象，并在此处再次实现对歌曲文件的查询
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

        //获取button对象，并添加点击事件
        Button start_btn = findViewById(R.id.start_button);
        start_btn.setOnClickListener(this);

        Button preview_btn = findViewById(R.id.preview_button);
        preview_btn.setOnClickListener(this);

        Button next_btn = findViewById(R.id.next_button);
        next_btn.setOnClickListener(this);

        //滑动菜单
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mMusicProgressSeekBar = findViewById(R.id.mySeekBar1);
        mMusicProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int currentProgress = 0; //记录进度的变量

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    currentProgress = progress;//设置变量记录进度，在完成的方法里实现音乐拖动，无卡顿
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMusicBinder.seekTo(currentProgress);
            }
        });

        if (ContextCompat.checkSelfPermission(MusicActivity.this, android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicActivity.this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {

            toStartService(position);
        }
    }


    public void toStartService(int position) {

        //发送当前歌曲在列表中的位置，让服务启动去播放
        Intent start = new Intent(MusicActivity.this, MusicService.class);
        start.putExtra("currentPosition", position);

        startService(start);
        LogUtil.d("Activity开始服务", "123asd");

        bindService(start, connection, BIND_AUTO_CREATE);
        LogUtil.d("Activity绑定服务", "123asd");
    }

    //完成音乐界面初始化，显示歌曲时长等静态信息
    private void initUI() {

        Song currentSong = mSong;
        LogUtil.d("Activity初始化界面", mSong.getSongName());

        //在TextView中显示歌曲时长
        TextView size = findViewById(R.id.size);

        Integer time = currentSong.getSize();
        Integer minute = time / 60000;
        String min = "0" + minute;

        Integer seconds = time % 60000 / 1000;
        String sec;
        if (seconds < 10) {
            sec = "0" + seconds;
        } else {
            sec = seconds.toString();
        }

        String musicLength = min + ":" + sec;

        size.setText(musicLength);

        //显示歌名
        TextView songTitle = findViewById(R.id.song_title);
        songTitle.setText(currentSong.getSongName());

    }

    //按钮事件处理
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    initUI();
                } else {
                    Toast.makeText(this, "拒绝权限无法使用", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
