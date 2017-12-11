package com.example.ts.songlist.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.ts.songlist.bean.Song;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ts on 17-11-23.
 * 用于获取手机中所有歌曲的信息
 */

public class AudioUtil {

    public static ArrayList<Song> getAllSongs(ContentResolver contentResolver) {

        ArrayList<Song> songs;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] columns = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,//文件名
                MediaStore.Audio.Media.TITLE,//歌曲名
                MediaStore.Audio.Media.DURATION,//时长
                MediaStore.Audio.Media.ARTIST,//歌手
                MediaStore.Audio.Media.ALBUM,//专辑
                MediaStore.Audio.Media.YEAR,//年份
                MediaStore.Audio.Media.MIME_TYPE,//类型
                MediaStore.Audio.Media.SIZE,//文件类型
                MediaStore.Audio.Media.DATA};//文件路径url

        String sql = MediaStore.Audio.Media.MIME_TYPE + "=? or "
                + MediaStore.Audio.Media.MIME_TYPE + "=? ";

        String[] args = {"audio/mpeg", "audio/x-ms-wma"};


        Cursor cursor = contentResolver.query(uri, columns, sql, args, null);

        songs = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            Song song;

            do {
                song = new Song();

                // 歌曲名
                String songName = cursor.getString(2);
                //歌手名
                String artist = cursor.getString(4);
                //时长
                int size = cursor.getInt(3);
                // 专辑名
                String album = cursor.getString(5);

                int id = cursor.getInt(0);
                LogUtil.d("search123", id + "");

                song.setmSize(size);
                song.setmSongName(songName);
                song.setmArtist(artist);
                song.setmAlbum(album);

                song.setId(id);
                // 文件路径
                if (cursor.getString(9) != null) {
                    song.setmFileUrl(cursor.getString(9));
                }

                song.save();
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();

        }
        return songs;
    }

}
