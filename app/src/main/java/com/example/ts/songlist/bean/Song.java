package com.example.ts.songlist.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by ts on 17-11-22.
 * It is a bean use to store the information of song
 */

public class Song  extends DataSupport implements Serializable{

    private int id;

    @Column(unique = true)
    private String mSongName;

    private String mArtist;
    private String mAlbum;
    private String mFileUrl;
    private int  mSize;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getmSize() {
        return mSize;
    }

    public void setmSize(int size) {
        this.mSize = size;
    }

    public String getmFileUrl() {
        return mFileUrl;
    }

    public void setmFileUrl(String mFileUrl) {
        this.mFileUrl = mFileUrl;
    }


    public String getmSongName() {
        return mSongName;
    }

    public void setmSongName(String mSongName) {
        this.mSongName = mSongName;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
