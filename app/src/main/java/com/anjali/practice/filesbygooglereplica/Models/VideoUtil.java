package com.anjali.practice.filesbygooglereplica.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.Contract;

public class VideoUtil implements Parcelable {

    public static final Creator<VideoUtil> CREATOR = new Creator<VideoUtil>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public VideoUtil createFromParcel(Parcel in) {
            return new VideoUtil(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public VideoUtil[] newArray(int size) {
            return new VideoUtil[size];
        }
    };

    //  Required Params for the Video Files
    private final String uri;
    private final int size;
    private final String name;
    private final long date;
    private final String location;

    // Constructor the same
    public VideoUtil(String uri, int size, String name, long date, String location) {
        this.uri = uri;
        this.size = size;
        this.name = name;
        this.date = date;
        this.location = location;
    }

    // Getters to get the values
    public long getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getUri() {
        return uri;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected VideoUtil(@NonNull Parcel in){
        uri = in.readString();
        size = in.readInt();
        name = in.readString();
        date = in.readLong();
        location = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeInt(size);
        dest.writeString(name);
        dest.writeLong(date);
        dest.writeString(location);
    }

    public static boolean isVideo(String name){
        String[] ext = {".3gp", ".mp4", ".mkv", ".webm"};
        for (String s : ext) if (name.endsWith(s)) return true;
        return false;
    }

    public static void shareVideo(String uri_string, Context context) {

        Uri uri = Uri.parse(uri_string);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("video/*");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);

    }

    public static void openWith(String uri_string, Context context){

    }

}
