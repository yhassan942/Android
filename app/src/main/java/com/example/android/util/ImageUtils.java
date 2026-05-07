package com.example.android.util;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ImageUtils {
    public static void loadThumbnail(Context ctx, String uriString, ImageView into, int pxSize) {
        Uri uri = Uri.parse(uriString);
        Glide.with(ctx)
                .load(uri)
                .centerCrop()
                .override(pxSize, pxSize)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(into);
    }

    public static void loadFull(Context ctx, String uriString, ImageView into) {
        Uri uri = Uri.parse(uriString);
        Glide.with(ctx)
                .load(uri)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(into);
    }
}
