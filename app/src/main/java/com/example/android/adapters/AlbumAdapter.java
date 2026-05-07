package com.example.android.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.model.Album;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.VH> {
    public interface OnClick { void onClick(Album album); }
    public interface OnLongClick { void onLongClick(Album album, View view); }

    private final List<Album> albums;
    private final OnClick click;
    private final OnLongClick longClick;

    public AlbumAdapter(List<Album> albums, OnClick c, OnLongClick lc) {
        this.albums = albums; this.click = c; this.longClick = lc;
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        Album a = albums.get(position);
        holder.tvName.setText(a.getName());
        holder.tvCount.setText(a.getPhotos().size() + " photos");
        holder.itemView.setOnClickListener(v -> click.onClick(a));
        holder.itemView.setOnLongClickListener(v -> { longClick.onLongClick(a, v); return true; });
    }

    @Override public int getItemCount() { return albums.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvCount;
        VH(View v) { super(v); tvName = v.findViewById(R.id.tvAlbumName); tvCount = v.findViewById(R.id.tvAlbumCount); }
    }
}
