package com.example.android.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private String name;
    private List<Photo> photos = new ArrayList<>();

    public Album() {}
    public Album(String name) { this.name = name; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public List<Photo> getPhotos() { return photos; }
    public void addPhoto(Photo p){ photos.add(p); }
    public void removePhoto(Photo p){ photos.remove(p); }
}
