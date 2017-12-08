package com.example.ts.songlist.views;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ts.songlist.MusicActivity;
import com.example.ts.songlist.R;
import com.example.ts.songlist.SearchActivity;
import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.utils.AudioUtil;
import com.example.ts.songlist.utils.LogUtil;
import com.example.ts.songlist.utils.SongAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ts on 17-12-7.
 * 用于显示音乐文件列表的碎片
 */

@SuppressLint("NewApi")
public class MusicListFragment extends Fragment {

    private ClearTextView mSearchText;
    private ListView mListView;
    private List<Song> mSongList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        mSearchText = view.findViewById(R.id.search);
        mListView = view.findViewById(R.id.list_view);
        mSongList = AudioUtil.getAllSongs(getContext().getContentResolver());
        SongAdapter mAdapter = new SongAdapter(getContext(), R.layout.song_item, mSongList);
        mListView.setAdapter(mAdapter);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSearchText.setAfterTextChangeListener(new ClearTextView.AfterTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {

                String partner = s.toString();
                mSongList = AudioUtil.getAllSongs(getContext().getContentResolver(), partner);

                SongAdapter adapter = new SongAdapter(getContext(), R.layout.song_item, mSongList);
                mListView.setAdapter(adapter);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity() instanceof SearchActivity) {
                    LogUtil.d("Fragment123", "这是从SearcheActivity到musicActivity");

                    Intent intent = new Intent(getContext(), MusicActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);
                } else if (getActivity() instanceof MusicActivity) {
                    LogUtil.d("Fragment123", "这是从musicActivity到musicService");

                    MusicActivity musicActivity = (MusicActivity) getActivity();
                    musicActivity.mDrawerLayout.closeDrawers();
                    musicActivity.toStartService(position);
                }
            }
        });
    }
}
