package com.anjali.practice.filesbygooglereplica.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;
import com.anjali.practice.filesbygooglereplica.viewHolder.ViewPagerViewHolder;

import org.jetbrains.annotations.NotNull;

public class PdfAdapter extends RecyclerView.Adapter<ViewPagerViewHolder> {

    private final Context mContext;
    private final String path;
    private final int size;

    public PdfAdapter(Context mContext, String path, int size) {
        this.mContext = mContext;
        this.path = path;
        this.size = size;
    }

    @NonNull
    @NotNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.show_image_card, parent, false);
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        Bitmap bitmap = MyCache.getInstance().retrieveBitmapFromCache(path + " " + position);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return size;
    }

}
