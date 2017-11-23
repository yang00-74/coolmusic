package com.example.ts.songlist;

/**
 * Created by ts on 17-11-22.
 */

public class Song {
    private String songName;
    private String artist;
    private String desc;
    private String fileUrl;

    public Song() {
    }

    public Song(String songName, String artist, String desc) {
        this.songName = songName;
        this.artist = artist;
        this.desc = desc;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
