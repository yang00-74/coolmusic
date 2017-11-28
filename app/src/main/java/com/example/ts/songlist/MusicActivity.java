package com.example.ts.songlist;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.service.MyService;
import com.example.ts.songlist.utils.AudioUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MusicActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int WHAT_UPDATE_PROGRESS = 0;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Intent intent;
    private ProgressBar progressBar;
    private boolean mIsRunning = true;
    private List<Song> mSongList = new ArrayList<>();
    private int mCurrent;//记录当前在播放的音乐在列表中的下标

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_UPDATE_PROGRESS:
                    if (mIsRunning) {
                        double current = mediaPlayer.getCurrentPosition();
                        double total = mediaPlayer.getDuration();
                        progressBar.setProgress((int) (current / total * 1000d));
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
        intent = getIntent();
        mCurrent = intent.getIntExtra("position", 0);
        mSongList = AudioUtils.getAllSongs(MusicActivity.this.getContentResolver());

        //获取button对象，并添加点击事件
        Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(this);

        Button stop = (Button) findViewById(R.id.stop_button);
        stop.setOnClickListener(this);

        Button preview = (Button) findViewById(R.id.preview_button);
        preview.setOnClickListener(this);

        Button next = (Button) findViewById(R.id.next_button);
        next.setOnClickListener(this);

        //开始服务按钮
        Button startService = (Button) findViewById(R.id.start_service);
        startService.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (ContextCompat.checkSelfPermission(MusicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicActivity.this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            initMediaPlayer();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();

                    mIsRunning = true;
                    new ProgressThread().start();

                } else {
                    mediaPlayer.pause();
                }
                break;
            case R.id.next_button:
                if (mCurrent < mSongList.size() - 1) {
                    mCurrent += 1;
                    mediaPlayer.reset();
                    initMediaPlayer();
                    mediaPlayer.start();
                    new ProgressThread().start();
                } else {
                    Toast.makeText(this, "没有下一首了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop_button:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                    initMediaPlayer();
                }
                break;

            case R.id.preview_button:
                if (mCurrent == 0) {
                    Toast.makeText(this, "没有上一首了", Toast.LENGTH_SHORT).show();
                } else {
                    mCurrent -= 1;
                    mediaPlayer.reset();
                    initMediaPlayer();
                    mediaPlayer.start();
                    new ProgressThread().start();
                }
                break;

            //开始服务事件
            case R.id.start_service:
                Intent satrt = new Intent(this, MyService.class);
                startService(satrt);
               // stopService(new Intent(this, MyService.class));

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
                    initMediaPlayer();
                } else {
                    Toast.makeText(this, "拒绝权限无法使用", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void initMediaPlayer() {
        try {
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

            //设置url音乐文件源
            String url = currentSong.getFileUrl();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
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
