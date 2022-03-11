package com.anjali.practice.filesbygooglereplica.loaders;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.anjali.practice.filesbygooglereplica.models.DocumentsUtil;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediaDocLoader extends AsyncTaskLoader<ArrayList<DocumentsUtil>> {

    private final String LOG_TAG = MediaDocLoader.class.getSimpleName();
    private final Context context;

    // constructor for docLoader
    public MediaDocLoader(@NonNull @NotNull Context context) {
        super(context);
        this.context = context;
    }

    // starting the background task
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    // during the background task
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public ArrayList<DocumentsUtil> loadInBackground() {

        ArrayList<DocumentsUtil> list = new ArrayList<>();

        final String[] projection = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.RELATIVE_PATH
        };

        final String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";

        String mime = MediaStore.Files.FileColumns.MIME_TYPE;
        final String selection = mime + " = ? OR " + mime + " = ? OR " + mime + " = ? ";

        final String pdfType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        final String docType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");
        final String xlsxType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx");
        final String[] selectionArgs = new String[]{pdfType, docType, xlsxType};

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                projection,
                selection,
                selectionArgs,
                sortOrder)) {
            assert cursor != null;
            int idColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
            int sizeColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
            int titleColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
            int locationColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH);
            int dateColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumnIndex);
                int size = cursor.getInt(sizeColumnIndex);
                String title = cursor.getString(titleColumnIndex);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), id);
                String location = cursor.getString(locationColumnIndex);
                long date = cursor.getLong(dateColumnIndex);

                ParcelFileDescriptor pdf;
                PdfRenderer pdfRenderer;
                try {
                    File file = new File( "/storage/emulated/0/" + location + "/" + title);
                    Log.e(LOG_TAG, file.getAbsolutePath());
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
                list.add(new DocumentsUtil(contentUri.toString(), size, title, date, location));

            }
        }

        return list;
    }

}
