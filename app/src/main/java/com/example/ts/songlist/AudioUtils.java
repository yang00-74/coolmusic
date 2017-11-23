package com.example.ts.songlist;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ts on 17-11-23.
 * 用于获取手机中所有歌曲的信息
 */

public class AudioUtils {
    public static ArrayList<Song> getAllSongs(ContentResolver contentResolver) {

        ArrayList<Song> songs = null;

        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[]{"audio/mpeg", "audio/x-ms-wma"}, null);

        songs = new ArrayList<>();

        if (cursor.moveToFirst()) {

            Song song = null;

            do {
                song = new Song();
                // 文件名
                //  song.setFileName(cursor.getString(1));
                // 歌曲名
                song.setSongName(cursor.getString(2));
                // 时长
                // song.setDuration(cursor.getInt(3));
                // 歌手名
                song.setArtist(cursor.getString(4));
                // 专辑名
                song.setDesc(cursor.getString(5));
                // 年代
//                if (cursor.getString(6) != null) {
//                    song.setYear(cursor.getString(6));
//                } else {
//                    song.setYear("未知");
//                }
//                // 歌曲格式
//                if ("audio/mpeg".equals(cursor.getString(7).trim())) {
//                    song.setType("mp3");
//                } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
//                    song.setType("wma");
//                }
//                // 文件大小
//                if (cursor.getString(8) != null) {
//                    float size = cursor.getInt(8) / 1024f / 1024f;
//                    song.setSize((size + "").substring(0, 4) + "M");
//                } else {
//                    song.setSize("未知");
//                }
                // 文件路径
                if (cursor.getString(9) != null) {
                    song.setFileUrl(cursor.getString(9));
                }
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();

        }
        return songs;
    }

}
