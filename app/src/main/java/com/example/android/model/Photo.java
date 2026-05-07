package com.example.android.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private String uriString;
    private String filename;
    private List<Tag> tags = new ArrayList<>();

    public Photo() {}
    public Photo(String uriString, String filename) {
        this.uriString = uriString;
        this.filename = filename;
    }

    public String getUriString() { return uriString; }
    public String getFilename() { return filename; }
    public List<Tag> getTags() { return tags; }

    public void addTag(Tag t){
        if (!tags.contains(t)) tags.add(t);
    }
    public void removeTag(Tag t){
        tags.remove(t);
    }
}