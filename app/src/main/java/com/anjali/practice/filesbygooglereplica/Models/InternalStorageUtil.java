package com.anjali.practice.filesbygooglereplica.models;

import android.os.Parcel;
import android.os.Parcelable;

public class InternalStorageUtil implements Parcelable {

    //  Required Params for the InternalStorage Files
    private final boolean isFolder;
    private final String name;
    private final String uri;
    private final long size;
    private final long date;

    // Constructor the same
    public InternalStorageUtil(boolean isFolder, String name, String uri, long size, long date) {
        this.isFolder = isFolder;
        this.name = name;
        this.uri = uri;
        this.size = size;
        this.date = date;
    }

    protected InternalStorageUtil(Parcel in) {
        isFolder = in.readByte() != 0;
        name = in.readString();
        uri = in.readString();
        size = in.readLong();
        date = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isFolder ? 1 : 0));
        dest.writeString(name);
        dest.writeString(uri);
        dest.writeLong(size);
        dest.writeLong(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InternalStorageUtil> CREATOR = new Creator<InternalStorageUtil>() {
        @Override
        public InternalStorageUtil createFromParcel(Parcel in) {
            return new InternalStorageUtil(in);
        }

        @Override
        public InternalStorageUtil[] newArray(int size) {
            return new InternalStorageUtil[size];
        }
    };

    // Getters to get the values
    public boolean isFolder() {
        return isFolder;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public long getSize() {
        return size;
    }

    public long getDate(){
        return date;
    }


}
