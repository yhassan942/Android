package com.example.android.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.model.Photo;
import com.example.android.util.ImageUtils;
import java.util.List;

public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.VH> {
    public interface OnClick { void onClick(int position); }
    public interface OnLongClick { void onLongClick(int position); }

    private final List<Photo> photos;
    private final OnClick click;
    private final OnLongClick longClick;
    private final android.content.Context ctx;

    public PhotoGridAdapter(android.content.Context ctx, List<Photo> photos, OnClick click, OnLongClick longClick) {
        this.ctx = ctx;
        this.photos = photos; this.click = click; this.longClick = longClick;
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_grid, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        Photo p = photos.get(position);
        ImageUtils.loadThumbnail(ctx, p.getUriString(), holder.iv, 250);
        holder.itemView.setOnClickListener(v -> click.onClick(position));
        holder.itemView.setOnLongClickListener(v -> { longClick.onLongClick(position); return true; });
    }

    @Override public int getItemCount() { return photos.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv;
        VH(View v){ super(v); iv = v.findViewById(R.id.ivThumb); }
    }
}

