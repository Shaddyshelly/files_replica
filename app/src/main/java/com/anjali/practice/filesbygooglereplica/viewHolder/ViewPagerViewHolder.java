package com.anjali.practice.filesbygooglereplica.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anjali.practice.filesbygooglereplica.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ViewPagerViewHolder extends RecyclerView.ViewHolder {

    public PhotoView imageView;

    public ViewPagerViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.show_image_view_pager_image);
    }
}
