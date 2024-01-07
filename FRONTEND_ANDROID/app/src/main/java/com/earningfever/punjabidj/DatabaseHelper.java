package com.earningfever.punjabidj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "songs_db";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Song.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Song.TABLE_NAME);
        onCreate(db);
    }

    public Boolean insertSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Song.COLUMN_ID, song.getId());
        values.put(Song.COLUMN_NAME, song.getSongName());
        values.put(Song.COLUMN_SONG_IMAGE_LINK,song.getSongImageLink());
        values.put(Song.COLUMN_SONG_LINK_ID,song.getSongLinkId());
        values.put(Song.COLUMN_SINGER_ID,song.getSingerId());

        long id_return = db.insert(Song.TABLE_NAME, null, values);
        db.close();

        if (id_return == -1) {
            return false;
        }
        return true;
    }

    public void deleteSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Song.TABLE_NAME, Song.COLUMN_ID + " = ?",
                new String[]{String.valueOf(song.getId())});
        db.close();
    }

    public Song getSong(String songName){
        Song song = null;

        //SELECT * FROM `Songs` WHERE Name LIKE "Sidhu Moosewala Song5"
        String selectQuery = "SELECT * FROM " + Song.TABLE_NAME + " WHERE " + Song.COLUMN_NAME + " LIKE " + "'" + songName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                song  = new Song(cursor.getInt(cursor.getColumnIndex(Song.COLUMN_ID)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_NAME)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_SONG_IMAGE_LINK)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_SONG_LINK_ID)),true,cursor.getInt(cursor.getColumnIndex(Song.COLUMN_SINGER_ID)));
            } while (cursor.moveToNext());
        }
        db.close();
        return song;
    }

    public ArrayList<Song> getAllSongs() {
        ArrayList<Song> songs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Song.TABLE_NAME + " ORDER BY " +
                Song.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song(cursor.getInt(cursor.getColumnIndex(Song.COLUMN_ID)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_NAME)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_SONG_IMAGE_LINK)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_SONG_LINK_ID)),true,cursor.getInt(cursor.getColumnIndex(Song.COLUMN_SINGER_ID)));
                songs.add(song);
            } while (cursor.moveToNext());
        }
        db.close();
        return songs;
    }
    public ArrayList<Song> getSingerSongs(int singerId){
        ArrayList<Song> songs  = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Song.TABLE_NAME + " WHERE " + Song.COLUMN_SINGER_ID + " = " + singerId ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song(cursor.getInt(cursor.getColumnIndex(Song.COLUMN_ID)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_NAME)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_SONG_IMAGE_LINK)),cursor.getString(cursor.getColumnIndex(Song.COLUMN_SONG_LINK_ID)),true,cursor.getInt(cursor.getColumnIndex(Song.COLUMN_SINGER_ID)));
                songs.add(song);
            } while (cursor.moveToNext());
        }
        db.close();
        return songs;
    }
}
