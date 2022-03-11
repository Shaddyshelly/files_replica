package com.anjali.practice.filesbygooglereplica.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.activities.ShowAudioActivity;
import com.anjali.practice.filesbygooglereplica.activities.ShowImageActivity;
import com.anjali.practice.filesbygooglereplica.activities.ShowPdfActivity;
import com.anjali.practice.filesbygooglereplica.activities.ShowVideoActivity;
import com.anjali.practice.filesbygooglereplica.models.AudioUtil;
import com.anjali.practice.filesbygooglereplica.models.DownloadUtils;
import com.anjali.practice.filesbygooglereplica.models.ImageUtil;
import com.anjali.practice.filesbygooglereplica.models.VideoUtil;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;
import com.anjali.practice.filesbygooglereplica.viewHolder.MediaViewHolder;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MediaDownloadAdapter extends RecyclerView.Adapter<MediaViewHolder> {

    private final ArrayList<DownloadUtils> downloadUtils;
    private final Context context;
    private final boolean isList;

    public MediaDownloadAdapter(Context context, ArrayList<DownloadUtils> downloadUtils, boolean isList) {
        this.context = context;
        this.downloadUtils = downloadUtils;
        this.isList = isList;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View layoutView;
        if(isList){
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_media, null, false);
        }else{
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_media, null, false);
        }
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new MediaViewHolder(layoutView, isList);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        if(isList){
            Date date = new Date(downloadUtils.get(position).getDate()*1000);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
            String dateText = df2.format(date);
            String size = getSize(downloadUtils.get(position).getSize());
//            setUpPopUp(holder, position);
            holder.linear.setOnClickListener(view -> startActivityForFile(position));
            holder.mediaDate.setText(dateText + ", " + size);
        }else{
            holder.mediaName.setShadowLayer(2, 1, 1, Color.BLACK);
            holder.mediaSize.setShadowLayer(2, 1, 1, Color.BLACK);
            int sizeInt = downloadUtils.get(position).getSize();
            String sizeString = getSize(sizeInt);
            holder.mediaSize.setText(sizeString);
            holder.relative.setOnClickListener(view -> startActivityForFile(position));
        }

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Uri contentUris = Uri.parse(downloadUtils.get(position).getUri());
        String name = downloadUtils.get(position).getName();
        if(name.endsWith(".pdf") || AudioUtil.isAudio(name)){
            String key = "/storage/emulated/0/" + downloadUtils.get(position).getLocation() + name;
            Bitmap bitmap = MyCache.getInstance().retrieveBitmapFromCache(key);

            Log.e("LOG", key);
            if(bitmap != null) holder.imageView.setImageBitmap(bitmap);
            else {
                if (name.endsWith(".pdf")) holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_document_24));
                else holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_audiotrack_24));
            }
        }else if(ImageUtil.isImage(name) || VideoUtil.isVideo(name)){
            Glide.with(context)
                    .load(contentUris)
                    .placeholder(context.getDrawable(R.drawable.ic_baseline_document_24))
                    .into(holder.imageView);
        }else holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_document_24));

        holder.mediaName.setText(name);
    }

    @Override
    public int getItemCount() {
        return downloadUtils.size();
    }

    @NotNull
    private String getSize(int size){
        float sizeFloat = (float) size / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);

        if(sizeFloat < 1024) return sizeFloat + "KB";

        sizeFloat = sizeFloat / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);
        return sizeFloat + "MB";
    }

    private void startActivityForFile(int position){
        String name = downloadUtils.get(position).getName();
        if (AudioUtil.isAudio(name)) {
            startAudioPlayer(position);
        } else if (VideoUtil.isVideo(name)) {
            startVideoPlayer(position);
        } else if (ImageUtil.isImage(name)) {
            startImageShower(position);
        } else if (name.endsWith(".pdf")){
            startPdfViewer(position);
        } else {
            Toast.makeText(context, "Can't find app to open the file", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPdfViewer(int position) {

        Intent intent = new Intent(context, ShowPdfActivity.class);
        String filePath = "storage/emulated/0/" + downloadUtils.get(position).getLocation() + downloadUtils.get(position).getName();
        intent.putExtra("pdf_uri", filePath);
        context.startActivity(intent);

    }

    private void startImageShower(int pos) {
        String name = downloadUtils.get(pos).getName();
        ArrayList<String> image_uris = new ArrayList<>();
        ArrayList<String> image_names = new ArrayList<>();
        ArrayList<Long> image_date = new ArrayList<>();
        ArrayList<String> image_location = new ArrayList<>();
        ArrayList<Integer> image_size = new ArrayList<>();

        int total = -1;
        int current = -1;

        for(DownloadUtils util: downloadUtils){
            String nam = util.getName();
            if(ImageUtil.isImage(nam)){
                total++;
                image_uris.add(util.getUri());
                image_location.add(util.getUri());
                image_names.add(util.getName());
                image_size.add(util.getSize());
                image_date.add(util.getDate());

                if(nam.equals(name)) current = total;
            }
        }

        long[] longList = new long[image_date.size()];

        for(int j = 0; j < image_date.size(); j++) longList[j] = image_date.get(j);

        Bundle bundle = new Bundle();

        bundle.putStringArrayList("image_uris", image_uris);
        bundle.putStringArrayList("image_name", image_names);
        bundle.putIntegerArrayList("image_size", image_size);
        bundle.putLongArray("image_date", longList);
        bundle.putStringArrayList("image_location", image_location);
        bundle.putInt("current_position", current);

        Intent intent = new Intent(context, ShowImageActivity.class);
        intent.putExtra("INFO", bundle);
        context.startActivity(intent);
    }

    private void startAudioPlayer(int pos) {
        String name = downloadUtils.get(pos).getName();

        int total = -1;
        int current = -1;

        ArrayList<String> audio_uris = new ArrayList<>();
        ArrayList<String> audio_location = new ArrayList<>();
        ArrayList<String> audio_names = new ArrayList<>();
        ArrayList<Integer> audio_size = new ArrayList<>();
        ArrayList<Long> audio_dates = new ArrayList<>();

        for (int i = 0; i < downloadUtils.size(); i++) {
            String n = downloadUtils.get(i).getName();
            if (AudioUtil.isAudio(n)) {
                total++;
                audio_uris.add(downloadUtils.get(i).getUri());
                audio_location.add(downloadUtils.get(i).getUri());
                audio_names.add(downloadUtils.get(i).getName());
                audio_size.add(downloadUtils.get(i).getSize());
                audio_dates.add(downloadUtils.get(i).getDate());
                if (n.equals(name)) current = total;
            }
        }

        long[] audio_date = new long[audio_dates.size()];

        for (int j = 0; j < audio_dates.size(); j++) audio_date[j] = audio_dates.get(j);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("uris", audio_uris);
        bundle.putStringArrayList("location", audio_location);
        bundle.putStringArrayList("names", audio_names);
        bundle.putIntegerArrayList("size", audio_size);
        bundle.putLongArray("dates", audio_date);
        bundle.putInt("position", current);

        Intent intent = new Intent(context, ShowAudioActivity.class);
        intent.putExtra("INFO", bundle);
        context.startActivity(intent);
    }

    private void startVideoPlayer(int pos) {
        String name = downloadUtils.get(pos).getName();

        int total = -1;
        int current = -1;

        ArrayList<String> video_uris = new ArrayList<>();
        ArrayList<String> video_names = new ArrayList<>();
        ArrayList<String> video_location = new ArrayList<>();
        ArrayList<Integer> video_size = new ArrayList<>();
        ArrayList<Long> video_date = new ArrayList<>();

        for(DownloadUtils utils: downloadUtils){
            String n = utils.getName();
            if (VideoUtil.isVideo(n)) {
                total++;
                video_size.add(utils.getSize());
                video_uris.add(utils.getUri());
                video_names.add(utils.getName());
                video_location.add(utils.getUri());
                video_date.add(utils.getDate());

                if (n.equals(name)) current = total;
            }
        }

        long[] video_dates = new long[video_date.size()];

        for (int j = 0; j < video_date.size(); j++) video_dates[j] = video_date.get(j);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("video_uris", video_uris);
        bundle.putStringArrayList("video_name", video_names);
        bundle.putStringArrayList("video_location", video_location);
        bundle.putLongArray("video_date", video_dates);
        bundle.putIntegerArrayList("video_size", video_size);
        bundle.putInt("current_position", current);

        Intent intent = new Intent(context, ShowVideoActivity.class);
        intent.putExtra("INFO", bundle);
        context.startActivity(intent);
    }

}
