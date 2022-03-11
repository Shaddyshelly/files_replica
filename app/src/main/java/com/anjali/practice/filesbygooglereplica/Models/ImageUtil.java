package com.anjali.practice.filesbygooglereplica.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ImageUtil implements Parcelable {

    public static final Parcelable.Creator<ImageUtil> CREATOR = new Parcelable.Creator<ImageUtil>() {
        public ImageUtil createFromParcel(Parcel in) {
            return new ImageUtil(in);
        }

        public ImageUtil[] newArray(int size) {
            return new ImageUtil[size];
        }

    };

    //  Required Params for the Image Files
    private final String uri;
    private final int size;
    private final String name;
    private final long date;
    private final String path;

    // Constructor for the same
    public ImageUtil(String uri, int size, String name, long date, String path) {
        this.uri = uri;
        this.size = size;
        this.name = name;
        this.date = date;
        this.path = path;
    }

    // Getters to get the values
    public String getUri() {
        return uri;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    protected ImageUtil(Parcel in){
        super();
        uri = in.readString();
        size = in.readInt();
        name = in.readString();
        date = in.readLong();
        path = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeInt(size);
        dest.writeString(name);
        dest.writeLong(date);
        dest.writeString(path);
    }

    public static boolean isImage(String name){
        String[] ext = {".jpg", ".png", ".gif", ".bmp", ".webp", ".heic", ".heif"};
        for (String s : ext) if (name.endsWith(s)) return true;
        return false;
    }

    public static void shareImage(String uri_string, Context context){
        Uri uri = Uri.parse(uri_string);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/*");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }

    public static void openWith(String uri_String, Context context){
        Log.e("Log", uri_String);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(uri_String);
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
