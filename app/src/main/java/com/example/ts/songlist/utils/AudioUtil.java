package com.example.ts.songlist.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.ts.songlist.bean.Song;

import org.litepal.crud.DataSupport;

/**
 * Created by ts on 17-11-23.
 * It is used to search all local songs,
 * then insert each of them into database if it is not repeat
 */

public class AudioUtil {

    public static void getAllSongs(ContentResolver contentResolver) {

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
                MediaStore.Audio.Media.DATA,//文件路径url
                MediaStore.Audio.Media.IS_MUSIC//判断是否为音乐
        };

        String sql = "(" + MediaStore.Audio.Media.MIME_TYPE + "=? or "
                + MediaStore.Audio.Media.MIME_TYPE + "=? or "
                + MediaStore.Audio.Media.MIME_TYPE + "=?"
                + ") and "
                + MediaStore.Audio.Media.DURATION + " > 60000";

        String[] args = {"audio/mpeg", "audio/x-ms-wma", "audio/flac"};

        Cursor cursor = contentResolver.query(uri, columns, sql, args, null);


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

                song.setmSize(size);
                song.setmSongName(songName);
                song.setmArtist(artist);
                song.setmAlbum(album);

                // 文件路径
                if (cursor.getString(9) != null) {
                    song.setmFileUrl(cursor.getString(9));
                }

                if (song.isSaved()) {
                    Song savedSong = DataSupport.where("mSongName", song.getmSongName())
                            .find(Song.class).get(0);
                    song.update(savedSong.getId());
                } else {
                    song.save();
                }

            } while (cursor.moveToNext());

            cursor.close();
        }
    }

}
