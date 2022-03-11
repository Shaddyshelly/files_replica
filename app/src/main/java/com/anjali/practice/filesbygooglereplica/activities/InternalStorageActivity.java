package com.anjali.practice.filesbygooglereplica.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anjali.practice.filesbygooglereplica.listener.OnImageClickListener;
import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.adapters.InternalStorageAdapter;
import com.anjali.practice.filesbygooglereplica.loaders.InternalStorageLoader;
import com.anjali.practice.filesbygooglereplica.models.AudioUtil;
import com.anjali.practice.filesbygooglereplica.models.ImageUtil;
import com.anjali.practice.filesbygooglereplica.models.InternalStorageUtil;
import com.anjali.practice.filesbygooglereplica.models.VideoUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class InternalStorageActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<InternalStorageUtil>>, OnImageClickListener {

    private static final String LOG_TAG = InternalStorageActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private TextView nothing;
    private RecyclerView recyclerView;
    private static final int LOADER_ID = 35;
    private InternalStorageAdapter internalStorageAdapter;
    private ArrayList<InternalStorageUtil> utils;
    private ArrayList<InternalStorageUtil> permanent;
    private File visibleFileListParent;
    private LoaderManager loaderManager;
    private String HEAD_FILE;
    private boolean show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        initialiseRecyclerView();

        visibleFileListParent = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(
                getExternalFilesDir(null) // /storage/emulated/0/Android/data/com.anjali.practice.filesByGoogleReplica/file
                        .getParentFile()) // /storage/emulated/0/Android/data/com.anjali.practice.filesByGoogleReplica
                .getParentFile()) // /storage/emulated/0/Android/data
                .getParentFile()) // /storage/emulated/0/Android
                .getParentFile(); // /storage/emulated/0

        assert visibleFileListParent != null;
        HEAD_FILE = visibleFileListParent.getAbsolutePath();

        loaderManager = LoaderManager.getInstance(this);
        showFiles(0);

    }

    private void showFiles(int i) {
        if (i == 0) loaderManager.initLoader(LOADER_ID, null, this);
        else loaderManager.restartLoader(LOADER_ID, null, this);
    }

    @NonNull
    @NotNull
    @Override
    public Loader<ArrayList<InternalStorageUtil>> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        return new InternalStorageLoader(getApplicationContext(), visibleFileListParent);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<ArrayList<InternalStorageUtil>> loader, ArrayList<InternalStorageUtil> data) {

        progressBar.setVisibility(View.GONE);
        Log.e(LOG_TAG, "Done onLoadFinished");
        utils.clear();
        permanent.clear();
        permanent.addAll(data);
        if(!show){
            for(InternalStorageUtil util: permanent){
                if(!util.getName().startsWith(".")) utils.add(util);
            }
        }else{
            utils.addAll(permanent);
        }
        if (utils.size() == 0) {
            nothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            nothing.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        internalStorageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<ArrayList<InternalStorageUtil>> loader) {
        internalStorageAdapter = null;
    }

    @Override
    public void onBackPressed() {
        if (visibleFileListParent.getAbsolutePath().equals(HEAD_FILE)) {
            super.onBackPressed();
        } else {
            visibleFileListParent = visibleFileListParent.getParentFile();
            showFiles(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.internal_menu, menu);
        return true;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (visibleFileListParent.getAbsolutePath().equals(HEAD_FILE)) {
                finish();
            } else {
                visibleFileListParent = visibleFileListParent.getParentFile();
                showFiles(1);
            }
            return true;
        }else if(item.getItemId() == R.id.show_hide){
            utils.clear();
            if(show){
                item.setTitle(getString(R.string.show_private_files));
                for(InternalStorageUtil util: permanent){
                    if(!util.getName().startsWith(".")) utils.add(util);
                }
            }else{
                item.setTitle(getString(R.string.hide_private_files));
                utils.addAll(permanent);
            }
            internalStorageAdapter.notifyDataSetChanged();
            show = !show;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialiseRecyclerView() {
        progressBar = findViewById(R.id.media_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        nothing = findViewById(R.id.nothing);
        recyclerView = findViewById(R.id.media_recycler_view);
        recyclerView.setVisibility(View.GONE);
        nothing.setVisibility(View.GONE);
        utils = new ArrayList<>();
        permanent = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        internalStorageAdapter = new InternalStorageAdapter(this, utils, this);
        recyclerView.setAdapter(internalStorageAdapter);
    }

    @Override
    public void OnImageClick(int pos) {
        if (utils.get(pos).isFolder()) {
            visibleFileListParent = new File(utils.get(pos).getUri());
            showFiles(1);
        } else {
            String name = utils.get(pos).getName();

            if (AudioUtil.isAudio(name)) {
                startAudioPlayer(pos);
            } else if (VideoUtil.isVideo(name)) {
                startVideoPlayer(pos);
            } else if (ImageUtil.isImage(name)) {
                startImageShower(pos);
            } else if (name.endsWith(".pdf")) {
                startPdfShower(pos);
            } else {
                Toast.makeText(getApplicationContext(), "Can't find app to open the file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startPdfShower(int pos) {
        Intent intent = new Intent(this, ShowPdfActivity.class);
        intent.putExtra("pdf_uri", utils.get(pos).getUri());
        startActivity(intent);
    }

    private void startImageShower(int pos) {

        String name = utils.get(pos).getName();
        ArrayList<String> image_uris = new ArrayList<>();
        ArrayList<String> image_names = new ArrayList<>();
        ArrayList<Long> image_date = new ArrayList<>();
        ArrayList<String> image_location = new ArrayList<>();
        ArrayList<Integer> image_size = new ArrayList<>();

        int total = -1;
        int current = -1;

        for(InternalStorageUtil util: utils){
            String nam = util.getName();
            if(ImageUtil.isImage(nam)){
                total++;
                image_uris.add(util.getUri());
                image_location.add(util.getUri());
                image_names.add(util.getName());
                image_size.add((int) util.getSize());
                image_date.add(util.getDate());


                if(nam.equals(name)) current = total;
            }
        }

        long[] longList = new long[image_date.size()];

        for(int j = 0; j < image_date.size(); j++) longList[j] = image_date.get(j);

        Bundle bundle = new Bundle();

        bundle.putStringArrayList("image_uris", image_uris);
        bundle.putStringArrayList("image_name", image_names);
        bundle.putIntegerArrayList("image_size", image_size);
        bundle.putLongArray("image_date", longList);
        bundle.putStringArrayList("image_location", image_location);
        bundle.putInt("current_position", current);

        Intent intent = new Intent(InternalStorageActivity.this, ShowImageActivity.class);
        intent.putExtra("from_internal", bundle);
        startActivity(intent);
    }

    private void startAudioPlayer(int pos) {

        String name = utils.get(pos).getName();

        int total = -1;
        int current = -1;

        ArrayList<String> audio_uris = new ArrayList<>();
        ArrayList<String> audio_location = new ArrayList<>();
        ArrayList<String> audio_names = new ArrayList<>();
        ArrayList<Integer> audio_size = new ArrayList<>();
        ArrayList<Long> audio_dates = new ArrayList<>();

        for (int i = 0; i < utils.size(); i++) {
            String n = utils.get(i).getName();
            if (AudioUtil.isAudio(n)) {
                total++;
                audio_uris.add(utils.get(i).getUri());
                audio_location.add(utils.get(i).getUri());
                audio_names.add(utils.get(i).getName());
                audio_size.add((int)utils.get(i).getSize());
                audio_dates.add(utils.get(i).getDate());
                if (n.equals(name)) current = total;
            }
        }

        long[] audio_date = new long[audio_dates.size()];

        for (int j = 0; j < audio_dates.size(); j++) audio_date[j] = audio_dates.get(j);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("uris", audio_uris);
        bundle.putStringArrayList("location", audio_location);
        bundle.putStringArrayList("names", audio_names);
        bundle.putIntegerArrayList("size", audio_size);
        bundle.putLongArray("dates", audio_date);
        bundle.putInt("position", current);

        Intent intent = new Intent(InternalStorageActivity.this, ShowAudioActivity.class);
        intent.putExtra("from_internal", bundle);
        startActivity(intent);
    }

    private void startVideoPlayer(int pos) {

        String name = utils.get(pos).getName();

        int total = -1;
        int current = -1;

        ArrayList<String> video_uris = new ArrayList<>();
        ArrayList<String> video_names = new ArrayList<>();
        ArrayList<String> video_location = new ArrayList<>();
        ArrayList<Integer> video_size = new ArrayList<>();
        ArrayList<Long> video_date = new ArrayList<>();

        for (int i = 0; i < utils.size(); i++) {
            String n = utils.get(i).getName();
            if (VideoUtil.isVideo(n)) {
                total++;
                video_size.add((int)utils.get(i).getSize());
                video_uris.add(utils.get(i).getUri());
                video_names.add(utils.get(i).getName());
                video_location.add(utils.get(i).getUri());
                video_date.add(utils.get(i).getDate());

                if (n.equals(name)) current = total;
            }
        }

        long[] video_dates = new long[video_date.size()];

        for (int j = 0; j < video_date.size(); j++) video_dates[j] = video_date.get(j);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("video_uris", video_uris);
        bundle.putStringArrayList("video_name", video_names);
        bundle.putStringArrayList("video_location", video_location);
        bundle.putLongArray("video_date", video_dates);
        bundle.putIntegerArrayList("video_size", video_size);
        bundle.putInt("current_position", current);

        Intent intent = new Intent(InternalStorageActivity.this, ShowVideoActivity.class);
        intent.putExtra("from_internal", bundle);
        startActivity(intent);
    }
}
