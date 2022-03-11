package com.anjali.practice.filesbygooglereplica.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.anjali.practice.filesbygooglereplica.activities.ShowImageActivity;
import com.anjali.practice.filesbygooglereplica.models.ImageUtil;
import com.anjali.practice.filesbygooglereplica.viewHolder.MediaViewHolder;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MediaImageAdapter extends RecyclerView.Adapter<MediaViewHolder>{

    private final ArrayList<ImageUtil> imageUtil;
    private final Context context;
    private final boolean isList;

    public MediaImageAdapter(Context context, ArrayList<ImageUtil> imageUtil, boolean isList) {
        this.context = context;
        this.imageUtil = imageUtil;
        this.isList = isList;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            Date date = new Date(imageUtil.get(position).getDate()*1000);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
            String dateText = df2.format(date);
            String size = getSize(imageUtil.get(position).getSize());
            setUpPopUp(holder, position);
            holder.linear.setOnClickListener(view -> startImageActivity(position));
            holder.mediaDate.setText(dateText + ", " + size);
        }else{
            holder.mediaName.setShadowLayer(2, 1, 1, Color.BLACK);
            holder.mediaSize.setShadowLayer(2, 1, 1, Color.BLACK);
            int sizeInt = imageUtil.get(position).getSize();
            String sizeString = getSize(sizeInt);
            holder.mediaSize.setText(sizeString);
            holder.relative.setOnClickListener(view -> startImageActivity(position));
        }

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Uri contentUris = Uri.parse(imageUtil.get(position).getUri());
        Glide.with(context)
                .load(contentUris)
                .placeholder(context.getDrawable(R.drawable.ic_baseline_image_24))
                .into(holder.imageView);
        holder.mediaName.setText(imageUtil.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return imageUtil.size();
    }

    private void startImageActivity(int position){

        Intent intent = new Intent(context, ShowImageActivity.class);

        ArrayList<String> imageUris = new ArrayList<>();
        ArrayList<String> imageName = new ArrayList<>();
        ArrayList<Integer> imageSize = new ArrayList<>();
        ArrayList<Long> imageDate = new ArrayList<>();
        ArrayList<String> imageLocation = new ArrayList<>();

        for(ImageUtil util: imageUtil) {
            imageUris.add(util.getUri());
            imageName.add(util.getName());
            imageSize.add(util.getSize());
            imageDate.add(util.getDate());
            imageLocation.add(util.getPath());
        }

        long[] longList = new long[imageDate.size()];

        for(int j = 0; j < imageDate.size(); j++) longList[j] = imageDate.get(j);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("image_uris", imageUris);
        bundle.putStringArrayList("image_name", imageName);
        bundle.putIntegerArrayList("image_size", imageSize);
        bundle.putLongArray("image_date", longList);
        bundle.putStringArrayList("image_location", imageLocation);

        bundle.putInt("current_position", position);
        intent.putExtra("bundle", bundle);

        context.startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    private void setUpPopUp(MediaViewHolder holder, int position) {

        holder.listMore.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), holder.listMore);
            popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {

                switch (menuItem.getItemId()){
                    case R.id.share:
                        ImageUtil.shareImage(imageUtil.get(position).getUri(), context);
                        break;
                    case R.id.open_with:
                        imageOpenWith(position);
                        break;
                    case R.id.file_info:
                        infoImage(position);
                        break;
                    case R.id.delete_permanent:
                        deleteToast(imageUtil.get(position).getUri());
                        break;
                    default:
                        return false;
                }
                return true;
            });
            popupMenu.show();
        });

    }

    private void deleteToast(String uri){
        new AlertDialog.Builder(context)
                .setTitle("Delete Image")
                .setMessage("Do You really want to delete the image")
                .setPositiveButton("YES!!",
                        (dialog, which) -> Toast.makeText(context, "The file " + uri + " is deleted", Toast.LENGTH_SHORT).show())
                .setNegativeButton("NO!",
                        (dialog, which) -> Toast.makeText(context, "The file " + uri + " will not be deleted", Toast.LENGTH_SHORT).show())
                .create()
                .show();
    }

    private void imageOpenWith(int position) {
        ImageUtil.openWith(imageUtil.get(position).getUri(), context);
    }

    private void infoImage(int position) {
        Intent intent = new Intent(context, InfoActivity.class);
        Bundle bundle = new Bundle();
        String size_string = getSize(imageUtil.get(position).getSize());

        Date date = new Date(imageUtil.get(position).getDate() * 1000);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy, hh:mm:aa");
        String dateText = df2.format(date);
        bundle.putString("uri", imageUtil.get(position).getUri());
        bundle.putString("name", imageUtil.get(position).getName());
        bundle.putString("location", imageUtil.get(position).getPath());
        bundle.putString("time", dateText);
        bundle.putString("size", size_string);
        bundle.putInt("isMedia", 0);
        intent.putExtra("INFO", bundle);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

}
