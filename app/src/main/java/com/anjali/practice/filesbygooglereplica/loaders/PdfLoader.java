package com.anjali.practice.filesbygooglereplica.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.anjali.practice.filesbygooglereplica.utilities.MyCache;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;

public class PdfLoader extends AsyncTaskLoader<Integer> {

    private final String uri;

    // constructor for pdfloader
    public PdfLoader(@NonNull @NotNull Context context, String uri) {
        super(context);
        this.uri = uri;
    }

    // starting the background task
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    // background process
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Integer loadInBackground() {

        // getting file
        File file = new File(uri);
        ParcelFileDescriptor pdf;
        PdfRenderer pdfRenderer;
        try {
            pdf = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(pdf);

            final int pageCount = pdfRenderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);

                Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                // we render for showing on the screen

                Canvas canvas = new Canvas(mBitmap);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                MyCache.getInstance().saveBitmapToCache(file.getAbsolutePath() + " " + i, mBitmap);
                // close the page
                page.close();
            }
            return pageCount;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    }
}
