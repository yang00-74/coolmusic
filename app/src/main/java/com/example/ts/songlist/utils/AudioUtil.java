package com.example.ts.songlist.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.ts.songlist.bean.Song;

import java.util.ArrayList;

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
                //专辑名
                int size = cursor.getInt(3);

                String desc = cursor.getString(5);

                song.setSize(size);
                song.setSongName(songName);
                // 歌手名
                song.setArtist(artist);
                // 专辑名
                song.setDesc(desc);


                // 时长
//                // song.setDuration(cursor.getInt(3));
//                // 年代
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
//                // 文件路径
                if (cursor.getString(9) != null) {
                    song.setFileUrl(cursor.getString(9));
                }

                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();

        }
        return songs;
    }

    public static ArrayList<Song> getAllSongs(ContentResolver contentResolver, String partner) {

        ArrayList<Song> songs;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] columns = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA};
//mime_type title artist
        String sql = "(mime_type=? or mime_type=? ) and (title like ? or artist like ?)";
        String[] args = {"audio/mpeg", "audio/x-ms-wma", "%" + partner + "%", "%" + partner + "%"};


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
                //专辑名
                String desc = cursor.getString(5);
                //时长
                int size = cursor.getInt(3);

                song.setSongName(songName);
                // 歌手名
                song.setArtist(artist);
                // 专辑名
                song.setDesc(desc);

                song.setSize(size);

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
