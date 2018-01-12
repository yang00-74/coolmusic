package com.example.ts.songlist.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ts.songlist.MusicActivity;
import com.example.ts.songlist.R;
import com.example.ts.songlist.SearchActivity;
import com.example.ts.songlist.adapter.SongAdapter;
import com.example.ts.songlist.application.SongApplication;
import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ts on 17-12-7.
 * It is a fragment used to display the music list
 */

@SuppressLint("NewApi")
public class MusicListFragment extends Fragment {

    private ClearTextView mSearchText;

    public SongAdapter mAdapter;

    private static final String MUSICID = "musicId";

    private ListView mListView;

    private List<Song> mSongList = new ArrayList<>();

    private int mPosition = -1;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MUSICID.equals(intent.getAction())) {
                int position = intent.getIntExtra(MUSICID, 0);
                if (mPosition != position) {
                    mPosition = position;
                    mAdapter.setSelectedPosition(mPosition);
                    mAdapter.notifyDataSetInvalidated();
                }
            }

        }
    };

    public MusicListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        mSearchText = view.findViewById(R.id.search);
        mListView = view.findViewById(R.id.list_view);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            showList();
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSearchText.setAfterTextChangeListener(new ClearTextView.AfterTextChangeListener() {
            @Override
            public void afterTextChanged(Editable editable) {

                String partner = editable.toString();
                mSongList = SongApplication.getSongsManager().searchSongs(partner);
                mAdapter.clear();
                mAdapter.addAll(mSongList);
                mAdapter.notifyDataSetChanged();
            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Song song = mSongList.get(position);

                if (getActivity() instanceof SearchActivity) {

                    Intent intent = new Intent(getContext(), MusicActivity.class);
                    intent.putExtra(MUSICID, song.getId());
                    startActivity(intent);

                } else if (getActivity() instanceof MusicActivity) {

                    MusicActivity musicActivity = (MusicActivity) getActivity();
                    musicActivity.mDrawerLayout.closeDrawers();
                    musicActivity.toStartService(song.getId());
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MUSICID);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    public void showList() {
        //Every time when the app run ,init the database
        SongApplication.getSongsManager().init();
        mSongList = SongApplication.getSongsManager().findAllSong();

        for (Song song : mSongList) {
            LogUtil.d("search123", song.getId() + "");
        }
        mAdapter = new SongAdapter(getContext(), R.layout.song_item, mSongList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    showList();
                } else {
                    Toast.makeText(getContext(), "拒绝权限无法使用", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
        }
    }
}
