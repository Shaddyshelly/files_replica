package com.anjali.practice.filesbygooglereplica.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anjali.practice.filesbygooglereplica.R;

public class MediaViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public ImageView listMore;
    public TextView mediaName;
    public TextView mediaSize;
    public TextView mediaDate;
    public LinearLayout linear;
    public RelativeLayout relative;

    public MediaViewHolder(@NonNull View itemView, boolean isList) {
        super(itemView);
        if(isList){
            imageView = itemView.findViewById(R.id.list_image_view);
            mediaName = itemView.findViewById(R.id.media_name_list);
            linear = itemView.findViewById(R.id.list_linearLayout);
            listMore = itemView.findViewById(R.id.list_more);
            mediaDate = itemView.findViewById(R.id.media_size_date_list);
        }else{
            imageView = itemView.findViewById(R.id.grid_image_view);
            mediaName = itemView.findViewById(R.id.media_name_grid);
            mediaSize = itemView.findViewById(R.id.media_size);
            relative = itemView.findViewById(R.id.grid_layout);
        }
    }
}
