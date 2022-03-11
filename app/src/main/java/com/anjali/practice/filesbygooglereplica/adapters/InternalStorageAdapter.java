package com.anjali.practice.filesbygooglereplica.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anjali.practice.filesbygooglereplica.listener.OnImageClickListener;
import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.models.AudioUtil;
import com.anjali.practice.filesbygooglereplica.models.InternalStorageUtil;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;
import com.anjali.practice.filesbygooglereplica.viewHolder.InternalViewHolder;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class InternalStorageAdapter
        extends RecyclerView.Adapter<InternalViewHolder> {

    private final Context context;
    private final ArrayList<InternalStorageUtil> utils;
    private final OnImageClickListener onImageClickListener;

    public InternalStorageAdapter(Context context, ArrayList<InternalStorageUtil> utils, OnImageClickListener onImageClickListener) {
        this.context = context;
        this.utils = utils;
        this.onImageClickListener = onImageClickListener;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public InternalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_media, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new InternalViewHolder(layoutView, onImageClickListener);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull InternalViewHolder holder, int position) {
        holder.mediaName.setText(utils.get(position).getName());
        if (!utils.get(position).isFolder()) {
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (AudioUtil.isAudio(utils.get(position).getName())) {
                Bitmap bitmap = MyCache.getInstance().retrieveBitmapFromCache(utils.get(position).getUri() + utils.get(position).getName());

                if (bitmap != null) holder.imageView.setImageBitmap(bitmap);
                else
                    holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_audiotrack_24));

            } else if (utils.get(position).getName().endsWith(".pdf")) {
                Bitmap bitmap = MyCache.getInstance().retrieveBitmapFromCache(utils.get(position).getUri());

                if (bitmap != null) holder.imageView.setImageBitmap(bitmap);
                else
                    holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_document_24));

            } else
                Glide.with(context).load(utils.get(position).getUri()).error(R.drawable.ic_baseline_document_24).into(holder.imageView);

        } else {
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_folder_open_24));
        }

        holder.listMore.setVisibility(View.GONE);
        String sizeString = getSize(utils.get(position).getSize());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy, hh:mm:aa");
        String dateText = df2.format(utils.get(position).getDate());
        if (!utils.get(position).isFolder()) holder.mediaDate.setText(sizeString + ", " + dateText);
        else holder.mediaDate.setText(dateText);

    }

    @Override
    public int getItemCount() {
        return utils.size();
    }

    @NotNull
    private String getSize(long size) {
        float sizeFloat = (float) size / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);

        if (sizeFloat < 1024) return sizeFloat + "KB";

        sizeFloat = sizeFloat / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);
        if (sizeFloat < 1024) return sizeFloat + "MB";

        sizeFloat = sizeFloat / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);
        return sizeFloat + "GB";
    }

}
