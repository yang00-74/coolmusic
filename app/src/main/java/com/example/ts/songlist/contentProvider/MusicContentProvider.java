package com.example.ts.songlist.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MusicContentProvider extends ContentProvider {

    private static final int MUSIC_DIR = 0;

    private static final int MUSIC_ITEM = 1;

    public static final String AUTHORITY = "com.example.ts.songlist.provider";

    private static UriMatcher uriMatcher;

    private MyDatabaseHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "song", MUSIC_DIR);
        uriMatcher.addURI(AUTHORITY, "song/#", MUSIC_ITEM);
    }

    public MusicContentProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case MUSIC_DIR:
                deletedRows = db.delete("song", selection, selectionArgs);
                break;
            case MUSIC_ITEM:
                String MusicId = uri.getPathSegments().get(1);
                deletedRows = db.delete("song", "id=?", new String[]{MusicId});
                break;

            default:
                break;
        }
        return deletedRows;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MUSIC_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.ts.songlist.provider.song";
            case MUSIC_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.ts.songlist.provider.song";
            default:
                break;
        }
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)) {
            case MUSIC_DIR:
            case MUSIC_ITEM:
                long newMusicID = db.insert("song", null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/song/" + newMusicID);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        //database name must be the completed with .db
        dbHelper = new MyDatabaseHelper(getContext(), "coolmusic.db", null, 6);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            //Uri uri = Uri.parse("content://com.example.ts.songlist.provider/song");
            case MUSIC_DIR:

                cursor = db.query("song", projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MUSIC_ITEM:
                String MusicId = uri.getPathSegments().get(1);
                cursor = db.query("song", projection, "id=?", new String[]{MusicId},
                        null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (uriMatcher.match(uri)) {
            case MUSIC_DIR:
                updatedRows = db.update("song", values, selection, selectionArgs);
                break;
            case MUSIC_ITEM:
                String musicId = uri.getPathSegments().get(1);
                updatedRows = db.update("song", values, "id=?", new String[]{musicId});
                break;
            default:
                break;

        }
        return updatedRows;
    }
}
