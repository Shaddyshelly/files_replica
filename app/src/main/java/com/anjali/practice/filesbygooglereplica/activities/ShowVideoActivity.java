package com.anjali.practice.filesbygooglereplica.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.models.VideoUtil;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowVideoActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ArrayList<String> videoUris, videoNames, videoLocation;
    private ArrayList<Integer> videoSize;
    private long[] video_dates;
    private int position;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private boolean isMedia = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        playerView = findViewById(R.id.player_view);

        Button share = findViewById(R.id.video_share);
        Button delete = findViewById(R.id.video_delete);
        Button info = findViewById(R.id.video_info);

        Intent intent = getIntent();
        Bundle bundle;
        if(intent.hasExtra("INFO")) {
            bundle = intent.getBundleExtra("INFO");
            isMedia = true;
        } else {
            bundle = intent.getBundleExtra("from_internal");
            isMedia = false;
        }
        videoUris = bundle.getStringArrayList("video_uris");
        position = bundle.getInt("current_position");
        video_dates = bundle.getLongArray("video_date");
        videoLocation = bundle.getStringArrayList("video_location");
        videoNames = bundle.getStringArrayList("video_name");
        videoSize = bundle.getIntegerArrayList("video_size");

        info.setOnClickListener(view -> infoVideo());
        delete.setOnClickListener(view -> deleteVideo());
        share.setOnClickListener(view -> VideoUtil.shareVideo(videoUris.get(position), this));
    }

    private void deleteVideo() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Video")
                .setMessage("Do You really want to delete the video")
                .setPositiveButton("YES!!",
                        (dialog, which) -> Toast.makeText(this, "The file " + videoUris.get(position) + " is deleted", Toast.LENGTH_SHORT).show())
                .setNegativeButton("NO!",
                        (dialog, which) -> Toast.makeText(this, "The file " + videoUris.get(position) + " will not be deleted", Toast.LENGTH_SHORT).show())
                .create()
                .show();
    }

    private void infoVideo() {
        int n = position;
        Intent intent = new Intent(this, InfoActivity.class);
        Bundle bundle = new Bundle();

        Date date = new Date(video_dates[n]*1000);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy, hh:mm:aa");
        String dateText = df2.format(date);
        bundle.putString("uri", videoUris.get(n));
        bundle.putString("name", videoNames.get(n));
        bundle.putString("location", videoLocation.get(n));
        bundle.putString("time", dateText);
        bundle.putString("size", getSize(videoSize.get(n)));
        bundle.putInt("isMedia", (isMedia) ? 2 : 6);
        intent.putExtra("INFO", bundle);

        startActivity(intent);
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(videoUris.get(position));
        player.setMediaItem(mediaItem);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void hideSystemUI() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    @NotNull
    private String getSize(int size){
        float sizeFloat = (float) size / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);

        if(sizeFloat < 1024) return sizeFloat + "KB";

        sizeFloat = sizeFloat / 1024;
        sizeFloat = (float) (Math.round(sizeFloat * 100.0) / 100.0);
        return sizeFloat + "MB";
    }

}