package com.earningfever.punjabidj;

import java.io.Serializable;

public class SingerModel implements Serializable {
    private int id;
    private String name;
    private String imageLink;

    public SingerModel() {
    }


    public SingerModel(int id, String name, String imageLink) {
        this.id = id;
        this.name = name;
        this.imageLink = imageLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
