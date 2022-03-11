package com.anjali.practice.filesbygooglereplica.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class AudioUtil implements Parcelable {

    public static final Parcelable.Creator<AudioUtil> CREATOR = new Parcelable.Creator<AudioUtil>() {
        @NonNull
        @Contract("_ -> new")
        public AudioUtil createFromParcel(Parcel in) {
            return new AudioUtil(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        public AudioUtil[] newArray(int size) {
            return new AudioUtil[size];
        }

    };

    //  Required Params for the Audio Files
    private final String uri;
    private final int size;
    private final String name;
    private final long date;
    private final String location;

    // Constructor for the same
    public AudioUtil(String uri, int size, String name, String location, long date) {
        this.uri = uri;
        this.size = size;
        this.name = name;
        this.location = location;
        this.date = date;
    }

    // Getters to get the values
    public long getDate() {
        return date;
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

    public String getLocation() {
        return location;
    }

    protected AudioUtil(Parcel in){
        super();
        uri = in.readString();
        size = in.readInt();
        name = in.readString();
        date = in.readLong();
        location = in.readString();
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeInt(size);
        dest.writeString(name);
        dest.writeLong(date);
        dest.writeString(location);
    }

    public static boolean isAudio(String name) {

        String[] ext = {".mp3", ".wav", ".amr", ".flac", ".aac", ".ogg", ".opus"};
        for (String s : ext) if (name.endsWith(s)) return true;
        return false;
    }

    public static void shareAudio(String uri_string, Context context){
        Uri uri = Uri.parse(uri_string);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("audio/*");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }

}
