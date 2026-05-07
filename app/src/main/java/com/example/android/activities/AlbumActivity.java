package com.example.android.activities;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.PhotoGridAdapter;
import com.example.android.model.Album;
import com.example.android.model.AppData;
import com.example.android.model.Photo;
import com.example.android.util.AppState;
import com.example.android.util.DataStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    private static final int REQUEST_PICK_IMAGE = 1001;
    Album album;
    RecyclerView rv;
    PhotoGridAdapter adapter;
    AppData data;

    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_album);
        data = AppState.get().getData();

        String albumName = getIntent().getStringExtra("albumName");
        album = findAlbum(albumName);
        setTitle(albumName);

        rv = findViewById(R.id.rv_photos);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PhotoGridAdapter(this, album.getPhotos(),
                position -> {
                    // open PhotoActivity with index
                    Intent i = new Intent(AlbumActivity.this, com.example.android.activities.PhotoActivity.class);
                    i.putExtra("albumName", album.getName());
                    i.putExtra("photoIndex", position);
                    startActivity(i);
                },
                position -> showPhotoOptions(position)
        );
        rv.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_photo);
        fabAdd.setOnClickListener(v -> pickImage());
        findViewById(R.id.btnAlbumSearch).setOnClickListener(v -> startActivity(new Intent(this, com.example.android.activities.SearchActivity.class)));
    }

    private Album findAlbum(String name){
        for (Album a : data.getAlbums()) if (a.getName().equals(name)) return a;
        // fallback new album
        Album a = new Album(name);
        data.getAlbums().add(a);
        AppState.get().save();
        return a;
    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (dataIntent == null) return;
            Uri uri = dataIntent.getData();
            if (uri == null) return;
            // persist permission
            final int takeFlags = dataIntent.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, takeFlags);

            String filename = queryDisplayName(uri);
            Photo p = new Photo(uri.toString(), filename);
            album.addPhoto(p);
            AppState.get().save();
            adapter.notifyDataSetChanged();
        }
    }

    private String queryDisplayName(Uri uri) {
        String name = "photo";
        ContentResolver cr = getContentResolver();
        try (Cursor cursor = cr.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) name = cursor.getString(idx);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return name;
    }

    private void showPhotoOptions(int position) {
        String[] opts = {"Move to another album", "Remove photo"};
        new AlertDialog.Builder(this)
                .setItems(opts, (dialog, which) -> {
                    if (which == 0) movePhoto(position);
                    else removePhoto(position);
                }).show();
    }

    private void removePhoto(int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Remove photo?")
                .setMessage("Remove this photo from album?")
                .setPositiveButton("Remove", (d,w) -> {
                    album.getPhotos().remove(pos);
                    AppState.get().save();
                    adapter.notifyDataSetChanged();
                }).setNegativeButton("Cancel", null).show();
    }

    private void movePhoto(int pos) {
        List<String> names = new ArrayList<>();
        for (Album a : data.getAlbums()) {
            if (!a.getName().equals(album.getName())) names.add(a.getName());
        }
        if (names.isEmpty()) {
            new AlertDialog.Builder(this).setMessage("No other albums to move to. Create an album first.").setPositiveButton("OK", null).show();
            return;
        }
        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names));
        new AlertDialog.Builder(this)
                .setTitle("Move to...")
                .setView(spinner)
                .setPositiveButton("Move", (d,w) -> {
                    String chosen = (String)spinner.getSelectedItem();
                    Album dest = null;
                    for (Album a : data.getAlbums()) if (a.getName().equals(chosen)) { dest = a; break; }
                    if (dest != null) {
                        Photo p = album.getPhotos().remove(pos);
                        dest.addPhoto(p);
                        AppState.get().save();
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    @Override protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
