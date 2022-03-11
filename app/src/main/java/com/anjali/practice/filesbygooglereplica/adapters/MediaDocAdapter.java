package com.anjali.practice.filesbygooglereplica.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.activities.ShowPdfActivity;
import com.anjali.practice.filesbygooglereplica.models.DocumentsUtil;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;
import com.anjali.practice.filesbygooglereplica.viewHolder.MediaViewHolder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MediaDocAdapter extends RecyclerView.Adapter<MediaViewHolder> {

    private final Context context;
    private final ArrayList<DocumentsUtil> data;
    private static final String LOG_TAG = MediaDocAdapter.class.getSimpleName();
    private final boolean isList;
    private Toast mToast;

    public MediaDocAdapter(Context context, ArrayList<DocumentsUtil> data, boolean isList) {
        this.context = context;
        this.data = data;
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
            Date date = new Date(data.get(position).getDate()*1000);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
            String dateText = df2.format(date);
            String size = getSize(data.get(position).getSize());
//            setUpPop(holder, position);
            holder.linear.setOnClickListener(view -> startImageActivity(position));
            holder.mediaDate.setText(dateText + ", " + size);
        }else{
            holder.mediaName.setShadowLayer(2, 1, 1, Color.BLACK);
            holder.mediaSize.setShadowLayer(2, 1, 1, Color.BLACK);
            String sizeString = getSize(data.get(position).getSize());
            holder.mediaSize.setText(sizeString);
            holder.relative.setOnClickListener(view -> startImageActivity(position));
        }

        String key = "/storage/emulated/0/" + data.get(position).getLocation() + data.get(position).getName();
        Log.e(LOG_TAG, key);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap bitmap = MyCache.getInstance().retrieveBitmapFromCache(key);

        if(bitmap != null) holder.imageView.setImageBitmap(bitmap);
        else holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_document_24));

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String name = data.get(position).getName();
        holder.mediaName.setText(name);
        
    }

    private void startImageActivity(int position) {
        if(data.get(position).getName().endsWith(".pdf")) {
            Intent intent = new Intent(context, ShowPdfActivity.class);
            String filePath = "storage/emulated/0/" + data.get(position).getLocation() + data.get(position).getName();
            intent.putExtra("pdf_uri", filePath);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
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

//    @SuppressLint("NonConstantResourceId")
//    private void setupPopUp(MediaDocViewHolder convertView, int position) {
//
//        convertView.list_more.setOnClickListener(view -> {
//            PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), convertView.list_more);
//            popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
//
//            popupMenu.setOnMenuItemClickListener(menuItem -> {
//                switch (menuItem.getItemId()){
//                    case R.id.share:
//                        shareDocument(data.get(position).getUri());
//                        break;
//                    case R.id.open_with:
//                        docOpenWith(position);
//                        break;
//                    case R.id.file_info:
//                        infoDocument(position);
//                        break;
//                    case R.id.delete_permanent:
//                        deleteToast();
//                        break;
//                    case R.id.yes:
//                        deleteYesToast(data.get(position).getUri());
//                        break;
//                    case R.id.no:
//                        deleteNoToast(data.get(position).getUri());
//                        break;
//                    default:
//                        return false;
//                }
//                return true;
//            });
//            popupMenu.show();
//        });
//
//    }

    private void deleteToast(){
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(context,
                "Do you really want to delete doc", Toast.LENGTH_LONG);
        mToast.show();
    }

    private void docOpenWith(int position) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(data.get(position).getUri());
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't find any app");
            if(mToast != null) mToast.cancel();
            mToast = Toast.makeText(context, "No App found to complete the action", Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    private void shareDocument(String uri_string) {

        Uri uri = Uri.parse(uri_string);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("application/pdf");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);

    }

    private void infoDocument(int position) {
    }

}

/*
 @SuppressLint({"InflateParams", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ImageView imageView;

        if(isList) {
            imageView = convertView.findViewById(R.id.list_image_view);
            TextView name = convertView.findViewById(R.id.media_name_list);
            name.setText(data.get(position).getName());
            setupPopUp(convertView, position);
        }else{
            imageView = convertView.findViewById(R.id.grid_image_view);
            TextView sizeText = convertView.findViewById(R.id.media_size);
            sizeText.setShadowLayer(2, 1, 1, Color.BLACK);
            String size_text = getSize((int) data.get(position).getSize());
            sizeText.setText(size_text);
            TextView name = convertView.findViewById(R.id.media_name_grid);
            name.setText(data.get(position).getName());
            name.setShadowLayer(2, 1, 1, Color.BLACK);
        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap bitmap = data.get(position).getBitmap();
        if(bitmap != null) imageView.setImageBitmap(bitmap);
        else imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_document_24));

        return convertView;
    }
* */