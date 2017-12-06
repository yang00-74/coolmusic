package com.example.ts.songlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.utils.AudioUtil;
import com.example.ts.songlist.views.ClearTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ListView mListView;
    private ClearTextView mSearchText;
    private List<Song> songList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mListView = (ListView) findViewById(R.id.list_view);
        mSearchText = (ClearTextView) findViewById(R.id.search);

        songList = AudioUtil.getAllSongs(SearchActivity.this.getContentResolver());

        SongAdapter adapter = new SongAdapter(SearchActivity.this, R.layout.song_item, songList);

        mListView.setAdapter(adapter);

        //根据歌曲名和艺术家实现模糊搜索音乐文件
        mSearchText.setAfterTextChangeListener(new ClearTextView.AfterTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {

                String pattner = s.toString();
                songList = AudioUtil.getAllSongs(SearchActivity.this.getContentResolver(), pattner);

                SongAdapter adapter = new SongAdapter(SearchActivity.this, R.layout.song_item, songList);
                mListView.setAdapter(adapter);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, MusicActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

    }
}
