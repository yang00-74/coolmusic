package com.example.ts.songlist.application;

import android.database.Cursor;
import android.net.Uri;

import com.example.ts.songlist.bean.Artist;
import com.example.ts.songlist.bean.Song;
import com.example.ts.songlist.utils.AudioUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.example.ts.songlist.application.SongApplication.getContext;


public class SongsManager {

    private List<Song> mList = new ArrayList<>();

    private List<Artist> mArtists = new ArrayList<>();

    private Set<Artist> mSet = new TreeSet<>();

    //search all local music file to initialize the database
    public void init() {
        AudioUtil.getAllSongs(getContext().getContentResolver());
    }

    public List<Song> findAllSong() {

        mList = DataSupport.findAll(Song.class);
        return mList;
    }

    public List<Artist> findAllArtist() {
        Uri uri = Uri.parse("content://com.example.ts.songlist.provider/song");
        Cursor cursor = getContext().getContentResolver().query(uri, new String[]{"mArtist"},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            do {
                String artistName = cursor.getString(0);
                Artist artist = new Artist();
                artist.setName(artistName);
                mSet.add(artist);
            } while (cursor.moveToNext());
            cursor.close();
        }
        mArtists.clear();
        mArtists.addAll(mSet);
        return mArtists;
    }

    public int countSongsByArtist(Artist artist) {
        return DataSupport.where("mArtist = ?", artist.getName()).count(Song.class);
    }

    public List<Song> searchSongs(String partner) {

        mList = DataSupport.where(new String[]{"mSongName like ? or mArtist like ?", "%" + partner + "%", "%" + partner + "%"}).find(Song.class);
        return mList;
    }

    public List<Song> getSongsByArtist(String artist) {
        return DataSupport.where("mArtist = ?", artist).find(Song.class);
    }
}
