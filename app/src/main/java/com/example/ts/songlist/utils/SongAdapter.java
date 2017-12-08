package com.example.ts.songlist.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ts.songlist.R;
import com.example.ts.songlist.bean.Song;

import java.util.List;

/**
 * Created by ts on 17-11-22.
 */

public class SongAdapter extends ArrayAdapter<Song> {
    private int resourceId;

    public SongAdapter(Context context, int textViewResourceId, List<Song> obj) {
        super(context, textViewResourceId, obj);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null, false);
            viewHolder = new ViewHolder();
            viewHolder.songName = view.findViewById(R.id.song_name);
            viewHolder.artist = view.findViewById(R.id.song_artist);
            viewHolder.desc = view.findViewById(R.id.song_desc);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.songName.setText(song.getSongName());
        viewHolder.artist.setText(song.getArtist());
        viewHolder.desc.setText(song.getDesc());

        return view;
    }

    private class ViewHolder {
        TextView artist;
        TextView desc;
        TextView songName;
    }
}
