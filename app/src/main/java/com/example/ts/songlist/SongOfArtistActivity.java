package com.example.ts.songlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ts.songlist.adapter.SongAdapter;
import com.example.ts.songlist.application.SongApplication;
import com.example.ts.songlist.bean.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ts on 17-12-22.
 * display the artist`s songs
 */

public class SongOfArtistActivity extends BasicActivity {

    private ListView mListView;

    private SongAdapter mAdapter;

    private List<Song> mSongList = new ArrayList<>();

    private final static String ARTIST = "artist";

    private static final String MUSICID = "musicId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songofartist);

        Intent intent = getIntent();
        String artist = "";
        if (null != intent && intent.hasExtra(ARTIST)) {
            artist = intent.getStringExtra(ARTIST);
        }

        mListView = findViewById(R.id.song_of_artist_list);

        mSongList = SongApplication.getSongsManager().getSongsByArtist(artist);

        mAdapter = new SongAdapter(this, R.layout.song_item, mSongList);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = mSongList.get(position);
                Intent start = new Intent(SongOfArtistActivity.this, MusicActivity.class);
                start.putExtra(MUSICID, song.getId());
                startActivity(start);
            }
        });

    }
}
