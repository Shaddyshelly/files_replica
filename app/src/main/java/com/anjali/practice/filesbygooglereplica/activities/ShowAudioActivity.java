package com.anjali.practice.filesbygooglereplica.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.models.AudioUtil;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowAudioActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ArrayList<String> audioUris, audioLocations, audioNames;
    private ArrayList<Integer> audioSize;
    private long[] audio_dates;
    private int position;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private boolean isMedia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_audio);

        playerView = findViewById(R.id.player_view);

        Button share = findViewById(R.id.audio_share);
        Button delete = findViewById(R.id.audio_delete);
        Button info = findViewById(R.id.audio_info);

        Intent intent = getIntent();
        Bundle bundle;

        if(intent.hasExtra("INFO")) bundle = intent.getBundleExtra("INFO");
        else bundle = intent.getBundleExtra("from_internal");

        audioUris = bundle.getStringArrayList("uris");
        audio_dates = bundle.getLongArray("dates");
        audioLocations = bundle.getStringArrayList("location");
        audioNames = bundle.getStringArrayList("names");
        audioSize = bundle.getIntegerArrayList("size");
        position = bundle.getInt("position");

        isMedia = intent.hasExtra("INFO");

        info.setOnClickListener(view -> infoAudio());
        delete.setOnClickListener(view -> deleteAudio());
        share.setOnClickListener(view -> AudioUtil.shareAudio(audioUris.get(position), this));
    }

    private void deleteAudio() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Do You really want to delete the audio")
                .setPositiveButton("YES!!",
                        (dialog, which) -> Toast.makeText(this, "The file " + audioUris.get(position) + " is deleted", Toast.LENGTH_SHORT).show())
                .setNegativeButton("NO!",
                        (dialog, which) -> Toast.makeText(this, "The file " + audioUris.get(position) + " will not be deleted", Toast.LENGTH_SHORT).show())
                .create()
                .show();
    }

    private void infoAudio() {

        int n = position;
        Intent intent = new Intent(this, InfoActivity.class);
        Bundle bundle = new Bundle();

        Date date = new Date(audio_dates[n]);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy, hh:mm:aa");
        String dateText = df2.format(date);
        bundle.putString("uri", audioUris.get(n));
        bundle.putString("name", audioNames.get(n));
        bundle.putString("location", audioLocations.get(n));
        bundle.putString("time", dateText);
        bundle.putString("size", getSize(audioSize.get(n)));
        bundle.putInt("isMedia", (isMedia) ? 1 : 5);
        intent.putExtra("INFO", bundle);
        startActivity(intent);

    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(audioUris.get(position));
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
