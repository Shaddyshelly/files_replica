package com.anjali.practice.filesbygooglereplica.utilities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import androidx.collection.LruCache;

public class MyCache {

    @SuppressLint("StaticFieldLeak")
    private static MyCache instance;
    private final LruCache<Object, Object> lru;

    private MyCache() {
        lru = new LruCache<>(2048);

    }

    public static MyCache getInstance() {
        if (instance == null) instance = new MyCache();
        return instance;
    }

    public LruCache<Object, Object> getLru() {
        return lru;
    }

    public void saveBitmapToCache(String key, Bitmap bitmap){

        try {
            MyCache.getInstance().getLru().put(key, bitmap);
        }catch (Exception ignored){}
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public Bitmap retrieveBitmapFromCache(String key){
        try {
            return (Bitmap) MyCache.getInstance().getLru().get(key);
        }catch (Exception e){
            return null;
        }
    }


}
