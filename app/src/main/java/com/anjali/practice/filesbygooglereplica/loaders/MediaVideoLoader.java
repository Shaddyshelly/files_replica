package com.anjali.practice.filesbygooglereplica.loaders;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.anjali.practice.filesbygooglereplica.models.VideoUtil;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class MediaVideoLoader extends AsyncTaskLoader<ArrayList<VideoUtil>> {

    private final Context context;
    private final String LOG_TAG = MediaVideoLoader.class.getSimpleName();

    // constructor for videoloader
    public MediaVideoLoader(@NonNull @NotNull Context context) {
        super(context);
        this.context = context;
    }

    // starting of background task
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    // during the background task
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public ArrayList<VideoUtil> loadInBackground() {

        ArrayList<VideoUtil> list = new ArrayList<>();

        // details required for videofiles
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.RELATIVE_PATH
        };

        // cursor for files
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_MODIFIED + " DESC"
        )) {
            assert cursor != null;
            int idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            int sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
            int nameColumn = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
            int locationColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH);
            int dateColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                int size = cursor.getInt(sizeColumn);
                String name = cursor.getString(nameColumn);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                String location = cursor.getString(locationColumnIndex);
                long date = cursor.getLong(dateColumnIndex);

                // adding the details in list
                VideoUtil videoUtil = new VideoUtil(contentUri.toString(), size, name, date, location);
                list.add(videoUtil);

            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't Load Files");
        }

        Log.e(LOG_TAG, "Back process Done");

        return list;
        
    }
}
