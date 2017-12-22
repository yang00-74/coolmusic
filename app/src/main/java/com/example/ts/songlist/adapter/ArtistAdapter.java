package com.example.ts.songlist.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ts.songlist.R;
import com.example.ts.songlist.application.SongApplication;
import com.example.ts.songlist.bean.Artist;

import java.util.List;

/**
 * Created by ts on 17-12-21.
 *
 */

public class ArtistAdapter extends ArrayAdapter<Artist> {

    private static final String TOTAL = "首";

    private int resourceId;

    public ArtistAdapter(Context context, int textViewResourceId, List<Artist> obj) {
        super(context, textViewResourceId, obj);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Artist artist = getItem(position);
        View view;
        ArtistAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null, false);
            viewHolder = new ArtistAdapter.ViewHolder();
            viewHolder.artist = view.findViewById(R.id.artist_name);
            viewHolder.totalSongs = view.findViewById(R.id.total_songs);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ArtistAdapter.ViewHolder) view.getTag();
        }

        assert artist != null;
        viewHolder.artist.setText(artist.getName());

        int total = SongApplication.getSongsManager().countSongsByArtist(artist);
        viewHolder.totalSongs.setText(total + TOTAL);

        return view;
    }

    private class ViewHolder {
        TextView artist;
        TextView totalSongs;
    }
}
