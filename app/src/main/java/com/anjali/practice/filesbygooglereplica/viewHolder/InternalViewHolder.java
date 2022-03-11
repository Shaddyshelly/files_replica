package com.anjali.practice.filesbygooglereplica.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.listener.OnImageClickListener;

public class InternalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imageView;
    public ImageView listMore;
    public TextView mediaName;
    public TextView mediaDate;
    public LinearLayout linear;
    public OnImageClickListener onImageClickListener;

    public InternalViewHolder(@NonNull View itemView, OnImageClickListener onImageClickListener) {
        super(itemView);
        imageView = itemView.findViewById(R.id.list_image_view);
        mediaName = itemView.findViewById(R.id.media_name_list);
        linear = itemView.findViewById(R.id.list_linearLayout);
        listMore = itemView.findViewById(R.id.list_more);
        mediaDate = itemView.findViewById(R.id.media_size_date_list);
        this.onImageClickListener = onImageClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onImageClickListener.OnImageClick(getAdapterPosition());
    }

}
