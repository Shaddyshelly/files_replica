package com.anjali.practice.filesbygooglereplica.loaders;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.anjali.practice.filesbygooglereplica.models.AudioUtil;
import com.anjali.practice.filesbygooglereplica.models.DownloadUtils;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediaDownloadLoader extends AsyncTaskLoader<ArrayList<DownloadUtils>> {

    private static final String LOG_TAG = MediaDownloadLoader.class.getSimpleName();
    private final Context context;

    // constructor for downloadLoader
    public MediaDownloadLoader(@NonNull @NotNull Context context) {
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
    public ArrayList<DownloadUtils> loadInBackground() {
        ArrayList<DownloadUtils> list = new ArrayList<>();

        String[] projection = new String[]{
                MediaStore.Downloads._ID,
                MediaStore.Downloads.DISPLAY_NAME,
                MediaStore.Downloads.SIZE,
                MediaStore.Downloads.DATE_MODIFIED,
                MediaStore.Downloads.RELATIVE_PATH
        };

        try(Cursor cursor = context.getContentResolver().query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Downloads.DATE_MODIFIED + " DESC"
        )){
            assert cursor != null;
            int idColumn = cursor.getColumnIndex(MediaStore.Downloads._ID);
            int sizeColumn = cursor.getColumnIndex(MediaStore.Downloads.SIZE);
            int nameColumn = cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME);
            int dateColumn = cursor.getColumnIndex(MediaStore.Downloads.DATE_MODIFIED);
            int locationColumn = cursor.getColumnIndex(MediaStore.Downloads.RELATIVE_PATH);

            while (cursor.moveToNext()){
                long id = cursor.getLong(idColumn);
                int size = cursor.getInt(sizeColumn);
                String name = cursor.getString(nameColumn);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id);
                long date = cursor.getLong(dateColumn);
                String location = cursor.getString(locationColumn);
                File file = new File( "/storage/emulated/0/" + location + "/" + name);
                if(file.isDirectory()) continue;

                if(name.endsWith(".pdf")) {
                    ParcelFileDescriptor pdf;
                    PdfRenderer pdfRenderer;
                    try {
                        pdf = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                        pdfRenderer = new PdfRenderer(pdf);

                        PdfRenderer.Page page = pdfRenderer.openPage(0);

                        // getting bitmap
                        Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawColor(Color.WHITE);
                        canvas.drawBitmap(bitmap, 0, 0, null);
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                        MyCache.getInstance().saveBitmapToCache(file.getAbsolutePath(), bitmap);
                        page.close();

                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.toString());
                        e.printStackTrace();
                    }
                }else if(AudioUtil.isAudio(name)){
                    try {
                        Bitmap bitmap = ThumbnailUtils.createAudioThumbnail(file,
                                new Size(200, 200), null);
                        MyCache.getInstance().saveBitmapToCache(file.getAbsolutePath(), bitmap);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Bitmap not available");
                    }
                }
                DownloadUtils downloadUtils = new DownloadUtils(contentUri.toString(), size, name, date, location);
                list.add(downloadUtils);
            }

        }catch (Exception e){
            Log.e(LOG_TAG, "Can't Load files");
        }

        return list;
    }
}
