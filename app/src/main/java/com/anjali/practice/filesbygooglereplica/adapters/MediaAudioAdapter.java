package com.anjali.practice.filesbygooglereplica.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.activities.InfoActivity;
import com.anjali.practice.filesbygooglereplica.models.AudioUtil;
import com.anjali.practice.filesbygooglereplica.activities.ShowAudioActivity;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;
import com.anjali.practice.filesbygooglereplica.viewHolder.MediaViewHolder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MediaAudioAdapter extends RecyclerView.Adapter<MediaViewHolder> {

    public final String LOG_TAG = MediaAudioAdapter.class.getSimpleName();
    private final ArrayList<AudioUtil> audioUtils;
    private final Context context;
    private final boolean isList;

    public MediaAudioAdapter(Context context, ArrayList<AudioUtil> audioUtils, boolean isList) {
        this.context = context;
        this.audioUtils = audioUtils;
        this.isList = isList;
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView;
        if (isList)
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_media, null, false);
        else
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_media, null, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new MediaViewHolder(layoutView, isList);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        if (isList) {
            Date date = new Date(audioUtils.get(position).getDate() * 1000);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
            String dateText = df2.format(date);
            String size = getSize(audioUtils.get(position).getSize());
            setUpPop(holder, position);
            holder.linear.setOnClickListener(view -> startAudioActivity(position));
            holder.mediaDate.setText(dateText + ", " + size);
        } else {
            holder.mediaName.setShadowLayer(2, 1, 1, Color.BLACK);
            holder.mediaSize.setShadowLayer(2, 1, 1, Color.BLACK);
            int sizeInt = audioUtils.get(position).getSize();
            String sizeString = getSize(sizeInt);
            holder.mediaSize.setText(sizeString);
            holder.relative.setOnClickListener(view -> startAudioActivity(position));
        }

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String name = audioUtils.get(position).getName();
        if (name.length() > 0) holder.mediaName.setText(name);
        else holder.mediaName.setText("Residue file you must delete it");

        Bitmap bitmap = MyCache.getInstance().retrieveBitmapFromCache(audioUtils.get(position).getUri());
        if (bitmap != null) holder.imageView.setImageBitmap(bitmap);
        else holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_audiotrack_24));

    }

    @Override
    public int getItemCount() {
        return audioUtils.size();
    }

    private void startAudioActivity(int position) {
        Intent intent = new Intent(context, ShowAudioActivity.class);
        ArrayList<String> audioUris = new ArrayList<>();
        ArrayList<String> audioNames = new ArrayList<>();
        ArrayList<Integer> audioSize = new ArrayList<>();
        ArrayList<String> audioLocation = new ArrayList<>();
        ArrayList<Long> audioDate = new ArrayList<>();

        for (AudioUtil util : audioUtils) {
            audioUris.add(util.getUri());
            audioNames.add(util.getName());
            audioSize.add(util.getSize());
            audioLocation.add(util.getLocation());
            audioDate.add(util.getDate());
        }

        long[] audio_date = new long[audioDate.size()];

        for (int j = 0; j < audioDate.size(); j++) audio_date[j] = audioDate.get(j);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("uris", audioUris);
        bundle.putStringArrayList("location", audioLocation);
        bundle.putStringArrayList("names", audioNames);
        bundle.putIntegerArrayList("size", audioSize);
        bundle.putLongArray("dates", audio_date);
        bundle.putInt("position", position);

        intent.putExtra("INFO", bundle);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    private void setUpPop(MediaViewHolder holder, int position) {
        holder.listMore.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), holder.listMore);
            popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.share:
                        AudioUtil.shareAudio(audioUtils.get(position).getUri(), context);
                        break;
                    case R.id.open_with:
                        audioOpenWith(position);
                        break;
                    case R.id.file_info:
                        infoAudio(position);
                        break;
                    case R.id.delete_permanent:
                        deleteToast(audioUtils.get(position).getUri());
                        break;
                    default:
                        return false;
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void deleteToast(String uri) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Image")
                .setMessage("Do You really want to delete the audio")
                .setPositiveButton("YES!!",
                        (dialog, which) -> Toast.makeText(context, "The file " + uri + " is deleted", Toast.LENGTH_SHORT).show())
                .setNegativeButton("NO!",
                        (dialog, which) -> Toast.makeText(context, "The file " + uri + " will not be deleted", Toast.LENGTH_SHORT).show())
                .create()
                .show();
    }

    private void audioOpenWith(int position) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(audioUtils.get(position).getUri());
        intent.setDataAndType(uri, "audio/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void infoAudio(int position) {

        Intent intent = new Intent(context, InfoActivity.class);
        Bundle bundle = new Bundle();

        Date date = new Date(audioUtils.get(position).getDate() * 1000);
        bundle.putString("uri", audioUtils.get(position).getUri());
        bundle.putString("name", audioUtils.get(position).getName());
        bundle.putString("location", audioUtils.get(position).getLocation());
        bundle.putString("time", date.toString());
        bundle.putString("size", getSize(audioUtils.get(position).getSize()));
        bundle.putInt("isMedia", 1);
        intent.putExtra("INFO", bundle);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    @NotNull
    private String getSize(int size) {
        float sizeFloat = (float) size / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);

        if (sizeFloat < 1024) return sizeFloat + "KB";

        sizeFloat = sizeFloat / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);
        return sizeFloat + "MB";
    }

}
