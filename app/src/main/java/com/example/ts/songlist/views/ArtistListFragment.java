package com.example.ts.songlist.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ts.songlist.R;
import com.example.ts.songlist.SongOfArtistActivity;
import com.example.ts.songlist.adapter.ArtistAdapter;
import com.example.ts.songlist.application.SongApplication;
import com.example.ts.songlist.bean.Artist;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ts on 17-12-21.
 *
 */

public class ArtistListFragment extends Fragment {

    private ListView mListView;
    private List<Artist> mArtistList = new ArrayList<>();
    private ArtistAdapter mAdapter;

    private final static String ARTIST = "artist";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);

        mListView = view.findViewById(R.id.artist_list);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mArtistList = SongApplication.getSongsManager().findAllArtist();
        mAdapter = new ArtistAdapter(getContext(), R.layout.artist_item, mArtistList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = mArtistList.get(position);

                Intent intent = new Intent(getContext(), SongOfArtistActivity.class);
                intent.putExtra(ARTIST, artist.getName());
                startActivity(intent);
            }
        });
    }
}
