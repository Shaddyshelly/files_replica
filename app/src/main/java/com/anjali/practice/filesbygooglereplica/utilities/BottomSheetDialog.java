package com.anjali.practice.filesbygooglereplica.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.anjali.practice.filesbygooglereplica.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    TextView largestFirst, smallestFirst, newestFirst, oldestFirst, aToZ, zToA;
    public static final String IMAGE_TAG = "image";
    public static final String AUDIO_TAG = "audio";
    public static final String VIDEO_TAG = "video";
    public static final String DOCUMENT_TAG = "document";
    public static final String DOWNLOAD_TAG = "download";

    public static final String LARGEST_FIRST = "largest-first";
    public static final String SMALLEST_FIRST = "smallest-first";
    public static final String NEWEST_FIRST = "newest-first";
    public static final String OLDEST_FIRST = "oldest-first";
    public static final String A_TO_Z = "a-to-z";
    public static final String Z_TO_A = "z-to-a";

    public static final String TAG = "tag";

    public Context context;
    public String tag;

    public BottomSheetDialog(Context context, String tag){
        this.context = context;
        this.tag = tag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        largestFirst = view.findViewById(R.id.largest_first);
        smallestFirst = view.findViewById(R.id.smallest_first);
        newestFirst = view.findViewById(R.id.newest_first);
        oldestFirst = view.findViewById(R.id.oldest_first);
        aToZ = view.findViewById(R.id.a_to_z);
        zToA = view.findViewById(R.id.z_to_a);

        Intent intent = new Intent(tag);

        largestFirst.setOnClickListener(v -> {
            intent.putExtra(TAG, LARGEST_FIRST);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
        smallestFirst.setOnClickListener(v -> {
            intent.putExtra(TAG, SMALLEST_FIRST);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
        newestFirst.setOnClickListener(v -> {
            intent.putExtra(TAG, NEWEST_FIRST);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
        oldestFirst.setOnClickListener(v -> {
            intent.putExtra(TAG, OLDEST_FIRST);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
        aToZ.setOnClickListener(v -> {
            intent.putExtra(TAG, A_TO_Z);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
        zToA.setOnClickListener(v -> {
            intent.putExtra(TAG, Z_TO_A);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });

        return view;
    }
}
