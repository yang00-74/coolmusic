package com.example.ts.songlist.bean;

import android.support.annotation.NonNull;

/**
 * Created by ts on 17-12-21.
 */

public class Artist implements Comparable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int compareTo(@NonNull Object o) {
        Artist artist = (Artist) o;
        int num = this.name.compareTo(artist.name);
        return num;
    }
}
