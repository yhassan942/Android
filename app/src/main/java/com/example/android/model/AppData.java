package com.example.android.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppData implements Serializable {
    private List<Album> albums = new ArrayList<>();
    public List<Album> getAlbums() { return albums; }
}

