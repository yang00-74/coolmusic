package com.example.ts.songlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSongs();

        SongAdapter adapter = new SongAdapter(MainActivity.this,
                R.layout.song_item, songList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private void initSongs() {
        for (int i = 0; i < 3; i++) {
            Song s1 = new Song("Bad", "swift", "a good singer");
            Song s2 = new Song("Starts in the rain", "Aimer", "a japanse singer");
            Song s3 = new Song("Love story", "swift", "a American girl");

            songList.add(s1);
            songList.add(s2);
            songList.add(s3);
        }
    }
}
