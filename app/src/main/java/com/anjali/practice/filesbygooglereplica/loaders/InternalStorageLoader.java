package com.anjali.practice.filesbygooglereplica.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.loader.content.AsyncTaskLoader;

import com.anjali.practice.filesbygooglereplica.models.AudioUtil;
import com.anjali.practice.filesbygooglereplica.models.InternalStorageUtil;
import com.anjali.practice.filesbygooglereplica.utilities.MyCache;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Objects;

public class InternalStorageLoader extends AsyncTaskLoader<ArrayList<InternalStorageUtil>> {

    private static final String LOG_TAG = InternalStorageLoader.class.getSimpleName();
    private final File file;

    public InternalStorageLoader(@NonNull @NotNull Context context, File file) {
        super(context);
        this.file = file;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public ArrayList<InternalStorageUtil> loadInBackground() {
        ArrayList<InternalStorageUtil> list = new ArrayList<>();

        if(file.getAbsolutePath().equals("/storage/emulated/0/Android/data") || file.getAbsolutePath().equals("/storage/emulated/0/Android/obb")) return list;
        for(File f: Objects.requireNonNull(file.listFiles())){
            boolean isFolder = true;
            if(!f.isDirectory()) isFolder = false;

            String name = f.getName();
            String path = f.getAbsolutePath();
            long size = 0;
            try {
                size = Files.size(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            long date = 0;
            try {
                BasicFileAttributes attr = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
                date = attr.lastModifiedTime().toMillis();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String uri = f.getAbsolutePath();

            if(AudioUtil.isAudio(name)){
                if(MyCache.getInstance().retrieveBitmapFromCache(uri + name) == null) {
                    // loading bitmap
                    try {
                        Bitmap bitmap = ThumbnailUtils.createAudioThumbnail(new File(uri),
                                new Size(200, 200), null);
                        MyCache.getInstance().saveBitmapToCache(uri + name, bitmap);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Bitmap not available");
                    }
                }
            }else if(name.endsWith(".pdf")){
                ParcelFileDescriptor pdf;
                PdfRenderer pdfRenderer;
                try {
                    File file = new File( uri);
                    pdf = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                    pdfRenderer = new PdfRenderer(pdf);

                    PdfRenderer.Page page = pdfRenderer.openPage(0);

                    // getting bitmap
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    MyCache.getInstance().saveBitmapToCache(uri, bitmap);
                    page.close();

                } catch (IOException e) {
                    Log.e(LOG_TAG, e.toString());
                    e.printStackTrace();
                }
            }

            InternalStorageUtil util = new InternalStorageUtil(isFolder, name, uri, size, date);
            list.add(util);

        }

        list.sort((util1, util2) -> {
            if(util1.isFolder() && util2.isFolder()) {
                return util1.getName().compareTo(util2.getName());
            }else if(util1.isFolder() && !util2.isFolder()){
                return -1;
            }else if(!util1.isFolder() && util2.isFolder()){
                return 1;
            }else {
                return util1.getName().compareTo(util2.getName());
            }
        });
        return list;
    }

}
