package com.example.android.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.AlbumAdapter;
import com.example.android.model.Album;
import com.example.android.model.AppData;
import com.example.android.model.Photo;
import com.example.android.util.AppState;
import com.example.android.util.DataStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv;
    AlbumAdapter adapter;
    AppData data;

    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_main);
        DataStore ds = new DataStore(this);
        AppState.get().init(ds);
        data = AppState.get().getData();
        loadStockPhotos();

        rv = findViewById(R.id.rv_albums);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlbumAdapter(data.getAlbums(),
                album -> {
                    Intent i = new Intent(MainActivity.this, AlbumActivity.class);
                    i.putExtra("albumName", album.getName());
                    startActivity(i);
                },
                (album, view) -> showAlbumOptions(album)
        );
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_album);
        fab.setOnClickListener(v -> showCreateAlbumDialog());

        findViewById(R.id.btnSearchHome).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, com.example.android.activities.SearchActivity.class));
        });
    }

    private void showCreateAlbumDialog() {
        // Inflate custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_album, null);
        EditText et = dialogView.findViewById(R.id.etAlbumName);

        // Build dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("New Album")
                .setView(dialogView)
                .setPositiveButton("Create", null) // We'll override click later
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the positive button to prevent auto-dismiss on empty name
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = et.getText().toString().trim();
            if (!name.isEmpty()) {
                data.getAlbums().add(new Album(name));
                AppState.get().save();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                et.setError("Album name cannot be empty");
            }
        });
    }


    private void showAlbumOptions(Album album) {
        String[] opts = {"Rename", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(album.getName())
                .setItems(opts, (dialog, which) -> {
                    if (which == 0) showRenameDialog(album);
                    else showDeleteDialog(album);
                }).show();
    }

    private void showRenameDialog(Album album) {
        final EditText et = new EditText(this);
        et.setText(album.getName());
        new AlertDialog.Builder(this)
                .setTitle("Rename album")
                .setView(et)
                .setPositiveButton("OK", (d,w) -> {
                    String n = et.getText().toString().trim();
                    if (!n.isEmpty()) {
                        album.setName(n);
                        AppState.get().save();
                        adapter.notifyDataSetChanged();
                    }
                }).show();
    }

    private void showDeleteDialog(Album album) {
        new AlertDialog.Builder(this)
                .setTitle("Delete album?")
                .setMessage("Delete album '" + album.getName() + "'?")
                .setPositiveButton("Delete", (d,w) -> {
                    data.getAlbums().remove(album);
                    AppState.get().save();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void loadStockPhotos() {
        Album stock = null;
        for (Album a : data.getAlbums()) {
            if (a.getName().equals("Stock Photos")) {
                stock = a;
                break;
            }
        }

        if (stock == null) {
            stock = new Album("Stock Photos");
            data.getAlbums().add(stock);

            int[] images = {
                    R.drawable.stock_blue,
                    R.drawable.stock_cyan,
                    R.drawable.stock_gray,
                    R.drawable.stock_green,
                    R.drawable.stock_magenta,
                    R.drawable.stock_red,
                    R.drawable.stock_yellow
            };

            for (int id : images) {
                String uri = "android.resource://" + getPackageName() + "/" + id;
                String filename = getResources().getResourceEntryName(id) + ".png";

                stock.getPhotos().add(new Photo(uri, filename));
            }
            AppState.get().save();
        }
    }



    @Override protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
