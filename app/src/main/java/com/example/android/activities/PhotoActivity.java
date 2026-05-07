package com.example.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android.R;
import com.example.android.model.Album;
import com.example.android.model.Photo;
import com.example.android.model.Tag;
import com.example.android.util.AppState;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    private Album album;
    private int index;
    private ImageView iv;
    private TextView tvTags;
    private List<Photo> photos;

    // For picking images from device storage
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        String albumName = getIntent().getStringExtra("albumName");
        index = getIntent().getIntExtra("photoIndex", 0);
        album = findAlbum(albumName);
        photos = album.getPhotos();

        iv = findViewById(R.id.ivPhoto);
        tvTags = findViewById(R.id.tvTags);

        // Buttons
        findViewById(R.id.btnPrev).setOnClickListener(v -> showIndex(index - 1));
        findViewById(R.id.btnNext).setOnClickListener(v -> showIndex(index + 1));
        findViewById(R.id.btnAddTag).setOnClickListener(v -> showAddTagDialog());
        findViewById(R.id.btnDelTag).setOnClickListener(v -> showDeleteTagDialog());
        findViewById(R.id.btnMove).setOnClickListener(v -> showMoveDialog());
        findViewById(R.id.btnDeletePhoto).setOnClickListener(v -> deleteCurrentPhoto());
        findViewById(R.id.btnAddPhoto).setOnClickListener(v -> pickImage());

        // Image picker setup
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) addPhotoFromUri(uri);
                }
        );


        showIndex(index);
    }

    private Album findAlbum(String name){
        for (Album a : AppState.get().getData().getAlbums()) {
            if (a.getName().equals(name)) return a;
        }
        return null;
    }

    private void showIndex(int newIndex) {
        if (album == null || photos.isEmpty()) return;
        if (newIndex < 0) newIndex = photos.size() - 1;
        if (newIndex >= photos.size()) newIndex = 0;
        index = newIndex;
        displayCurrent();
    }

    private void displayCurrent() {
        if (photos.isEmpty()) return;
        Photo p = photos.get(index);
        setTitle(p.getFilename());
        com.example.android.util.ImageUtils.loadFull(this, p.getUriString(), iv);
        refreshTags();
    }

    private void refreshTags() {
        Photo p = photos.get(index);
        StringBuilder sb = new StringBuilder();
        for (Tag t : p.getTags()) sb.append(t.getType().name()).append(": ").append(t.getValue()).append("\n");
        tvTags.setText(sb.length() == 0 ? "No tags" : sb.toString());
    }

    private void showAddTagDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_tag, null);
        Spinner sp = v.findViewById(R.id.spTagType);
        EditText et = v.findViewById(R.id.etTagValue);
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Person", "Location"}));

        new AlertDialog.Builder(this)
                .setTitle("Add Tag")
                .setView(v)
                .setPositiveButton("Add", (d,w) -> {
                    String val = et.getText().toString().trim();
                    if (!val.isEmpty()) {
                        Tag.Type type = sp.getSelectedItemPosition() == 0 ? Tag.Type.PERSON : Tag.Type.LOCATION;
                        photos.get(index).addTag(new Tag(type, val));
                        AppState.get().save();
                        refreshTags();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteTagDialog() {
        Photo p = photos.get(index);
        if (p.getTags().isEmpty()) {
            Toast.makeText(this, "No tags", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] items = new String[p.getTags().size()];
        for (int i = 0; i < p.getTags().size(); i++)
            items[i] = p.getTags().get(i).getType().name() + ": " + p.getTags().get(i).getValue();

        new AlertDialog.Builder(this)
                .setTitle("Delete Tag")
                .setItems(items, (d,w) -> {
                    p.getTags().remove(w);
                    AppState.get().save();
                    refreshTags();
                })
                .show();
    }

    private void showMoveDialog() {
        List<Album> albums = new ArrayList<>(AppState.get().getData().getAlbums());
        albums.remove(album);
        if (albums.isEmpty()) {
            Toast.makeText(this, "No other albums to move to", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = albums.stream().map(Album::getName).toArray(String[]::new);

        new AlertDialog.Builder(this)
                .setTitle("Move to album")
                .setItems(names, (d, which) -> {
                    Album dest = albums.get(which);
                    Photo p = photos.get(index);
                    photos.remove(index);
                    dest.getPhotos().add(p);
                    AppState.get().save();
                    Toast.makeText(this, "Moved to " + dest.getName(), Toast.LENGTH_SHORT).show();
                    if (photos.isEmpty()) finish();
                    else showIndex(index);
                })
                .show();
    }

    private void deleteCurrentPhoto() {
        if (photos.isEmpty()) return;
        photos.remove(index);
        AppState.get().save();
        Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
        if (photos.isEmpty()) finish();
        else showIndex(index);
    }

    private void pickImage() {
        pickImageLauncher.launch("image/*");
    }

    private void addPhotoFromUri(Uri uri) {
        String filename = uri.getLastPathSegment();
        Photo p = new Photo(uri.toString(), filename);
        photos.add(p);
        AppState.get().save();
        index = photos.size() - 1;
        showIndex(index);
    }
}
