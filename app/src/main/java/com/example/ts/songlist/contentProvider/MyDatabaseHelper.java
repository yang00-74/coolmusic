package com.example.ts.songlist.contentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ts on 17-12-11.
 * It is a class created to support getting database object
 */


public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_MUSIC_TABLE = "create table if not exists song ("
            + "id integer primary key autoincrement,"
            + "mSize integer,"
            + "mArtist text,"
            + "mAlbum text,"
            + "mFileUrl text)";

    private Context mContext;

    MyDatabaseHelper(Context context, String dbName,
                     SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (null == db) {
            return;
        }
        db.execSQL(CREATE_MUSIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
