package com.example.android.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.model.Photo;
import com.example.android.util.ImageUtils;
import android.widget.ImageView;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.VH> {
    public interface OnClick { void onClick(int adapterPosition); }

    private final Context ctx;
    private final List<SearchResult> results;
    private final OnClick click;

    public static class SearchResult {
        public final String albumName;
        public final int photoIndex;
        public final Photo photo;
        public SearchResult(String albumName, int photoIndex, Photo photo) {
            this.albumName = albumName; this.photoIndex = photoIndex; this.photo = photo;
        }
    }

    public SearchResultsAdapter(Context ctx, List<SearchResult> results, OnClick click){
        this.ctx = ctx; this.results = results; this.click = click;
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        SearchResult r = results.get(position);
        holder.tvAlbum.setText(r.albumName);
        holder.tvFilename.setText(r.photo.getFilename());
        ImageUtils.loadThumbnail(ctx, r.photo.getUriString(), holder.ivThumb, 200);
        holder.itemView.setOnClickListener(v -> click.onClick(position));
    }

    @Override public int getItemCount() { return results.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvAlbum, tvFilename;
        VH(View v){
            super(v);
            ivThumb = v.findViewById(R.id.ivResultThumb);
            tvAlbum = v.findViewById(R.id.tvResultAlbum);
            tvFilename = v.findViewById(R.id.tvResultFilename);
        }
    }
}
