package com.earningfever.punjabidj;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Song  implements Serializable {

    public static  String TABLE_NAME = "Songs";
    public static String COLUMN_ID = "Id";
    public static String COLUMN_NAME = "Name";
    public static String COLUMN_SONG_IMAGE_LINK = "Image";
    public static String COLUMN_SONG_LINK_ID = "Link";
    public static String COLUMN_SINGER_ID = "SingerId";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY , " + COLUMN_NAME + " TEXT ," +
            COLUMN_SONG_IMAGE_LINK+" TEXT , " + COLUMN_SONG_LINK_ID +" TEXT , " + COLUMN_SINGER_ID +" INTEGER " +")";


    private int id;
    private String songName;
    private String songImageLink;
    private String songLinkId;
    private boolean isFavorite;
    private int singerId;

    public Song() {
    }

    public Song(int id, String songName, String songImageLink, String songLinkId, boolean isFavorite, int singerId) {
        this.id = id;
        this.songName = songName;
        this.songImageLink = songImageLink;
        this.songLinkId = songLinkId;
        this.isFavorite = isFavorite;
        this.singerId = singerId;
    }

    public int getSingerId() {
        return singerId;
    }

    public void setSingerId(int singerId) {
        this.singerId = singerId;
    }


    public String getSongImageLink() {
        return songImageLink;
    }

    public void setSongImageLink(String songImageLink) {
        this.songImageLink = songImageLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongLinkId() {
        return songLinkId;
    }

    public void setSongLinkId(String songLinkId) {
        this.songLinkId = songLinkId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Song)) {
            return false;
        }
        Song otherMember = (Song) obj;
        return Objects.equals(otherMember.getId(), getId());

    }
}
