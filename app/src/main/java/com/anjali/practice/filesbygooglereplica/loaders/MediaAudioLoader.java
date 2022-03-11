package com.anjali.practice.filesbygooglereplica.loaders;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.anjali.practice.filesbygooglereplica.models.AudioUtil;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class MediaAudioLoader extends AsyncTaskLoader<ArrayList<AudioUtil>> {

    private final Context context;
    private final String LOG_TAG = MediaAudioLoader.class.getSimpleName();

    // constructor for audio loader
    public MediaAudioLoader(@NonNull @NotNull Context context) {
        super(context);
        this.context = context;
    }

    // starting the background task
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    // during background task
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public ArrayList<AudioUtil> loadInBackground() {
        ArrayList<AudioUtil> list = new ArrayList<>();

        // details required for audio files
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.RELATIVE_PATH
        };

        // starting the cursor
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Audio.Media.TITLE + " ASC"
        )){
            assert cursor != null;
            int idColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int sizeColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int locationColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH);
            int dateColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);

            while (cursor.moveToNext()){
                long id = cursor.getLong(idColumnIndex);
                int size = cursor.getInt(sizeColumnIndex);
                String title = cursor.getString(titleColumnIndex);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                String location = cursor.getString(locationColumnIndex);
                long date = cursor.getLong(dateColumnIndex);

                AudioUtil audioUtil;

                if(MyCache.getInstance().retrieveBitmapFromCache(contentUri.toString()) == null) {
                    // loading bitmap
                    try {
                        Bitmap bitmap = context.getContentResolver().loadThumbnail(
                                contentUri,
                                new Size(200, 200),
                                null
                        );
                        MyCache.getInstance().saveBitmapToCache(contentUri.toString(), bitmap);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Bitmap not available");
                    }
                }

//                 adding details in list
                audioUtil = new AudioUtil(contentUri.toString(), size, title, location, date);
                list.add(audioUtil);
            }

        }catch (Exception e) {
            Log.e(LOG_TAG, "Can't Load Files");
        }

        return list;
    }

}
