package com.example.ts.songlist;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.service.MusicService;
import com.example.ts.songlist.utils.AudioUtil;
import com.example.ts.songlist.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MusicActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int WHAT_UPDATE_PROGRESS = 0;

    private MusicService musicService;
    private MusicService.MusicBinder musicBinder;
    private SeekBar seekBar;
    private boolean mIsRunning = true;
    private List<Song> mSongList = new ArrayList<>();
    private int mCurrent;//记录当前在播放的音乐在列表中的下标

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
            musicService=musicBinder.getMusicService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_UPDATE_PROGRESS:
                    if (mIsRunning) {
                        //更新进度条
                        int current = musicService.mediaPlayer.getCurrentPosition();
                        int total = musicService.mediaPlayer.getDuration();
                        seekBar.setMax(total);
                        seekBar.setProgress(current);
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


        //获取intent对象，并在此处再次实现对歌曲文件的查询
        Intent intent = getIntent();
        mCurrent = intent.getIntExtra("position", 0);
        mSongList = AudioUtil.getAllSongs(MusicActivity.this.getContentResolver());

        //获取button对象，并添加点击事件
        Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(this);

        Button stop = (Button) findViewById(R.id.stop_button);
        stop.setOnClickListener(this);

        Button preview = (Button) findViewById(R.id.preview_button);
        preview.setOnClickListener(this);

        Button next = (Button) findViewById(R.id.next_button);
        next.setOnClickListener(this);

        seekBar = (SeekBar) findViewById(R.id.mySeekBar1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int currentProgress; //记录进度的变量

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    currentProgress = progress;//设置变量记录进度，在完成的方法里实现音乐拖动，无卡顿
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.mediaPlayer.seekTo(currentProgress);
            }
        });

        if (ContextCompat.checkSelfPermission(MusicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicActivity.this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            initUI();
            Intent start0 = new Intent(this, MusicService.class);
            start0.putExtra("position", mCurrent);
            startService(start0);
            bindService(start0, connection, BIND_AUTO_CREATE);


        }

    }

    //完成音乐初始化，并显示歌曲时长等静态信息
    private void initUI() {

        Song currentSong = mSongList.get(mCurrent);

        //在TextView中显示歌曲时长
        TextView size = (TextView) findViewById(R.id.size);

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
        size.setText(min + ":" + sec);

        //显示歌名
        TextView songTitle = (TextView) findViewById(R.id.song_title);
        songTitle.setText(currentSong.getSongName());

    }

    //按钮事件处理
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                if (!musicService.mediaPlayer.isPlaying()) {
                    musicService.mediaPlayer.start();
                    mIsRunning = true;

                } else {
                    musicService.mediaPlayer.pause();
                }
                break;

            case R.id.stop_button:
                if (musicService.mediaPlayer.isPlaying()) {
                    musicService.mediaPlayer.pause();
                }
                break;

            case R.id.next_button:
              //  musicBinder.nextMusic();
                musicService.next();
                break;

            case R.id.preview_button:
              //  musicBinder.previewMusic();
                musicService.preview();
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
        super.onDestroy();
        unbindService(connection);

        mIsRunning = false;
    }

    private class ProgressThread extends Thread {
        @Override
        public void run() {
            while (mIsRunning) {
                mHandler.sendEmptyMessage(WHAT_UPDATE_PROGRESS);

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
