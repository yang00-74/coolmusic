package com.example.ts.songlist.adapter;

import android.content.Context;
import android.graphics.Color;
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
 * It is created to adapter the song class
 */

public class SongAdapter extends ArrayAdapter<Song> {

    private int resourceId;
    private int selectedPosition;

    public SongAdapter(Context context, int textViewResourceId, List<Song> obj) {
        super(context, textViewResourceId, obj);
        resourceId = textViewResourceId;
        selectedPosition = -1;
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

        if (selectedPosition == position) {
            viewHolder.songName.setTextColor(Color.RED);
        } else {
            viewHolder.songName.setTextColor(Color.BLACK);
        }
        viewHolder.songName.setText(song.getmSongName());
        viewHolder.artist.setText(song.getmArtist());
        viewHolder.desc.setText(song.getmAlbum());

        return view;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    private class ViewHolder {
        TextView artist;
        TextView desc;
        TextView songName;
    }
}
