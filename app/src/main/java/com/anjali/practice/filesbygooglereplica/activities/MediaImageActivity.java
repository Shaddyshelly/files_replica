package com.anjali.practice.filesbygooglereplica.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.anjali.practice.filesbygooglereplica.utilities.BottomSheetDialog;
import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.adapters.MediaImageAdapter;
import com.anjali.practice.filesbygooglereplica.loaders.MediaImageLoader;
import com.anjali.practice.filesbygooglereplica.models.ImageUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MediaImageActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<ImageUtil>> {

    private final String LOG_TAG = MediaImageActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private static final int LoaderManger_ID = 10;
    private ArrayList<ImageUtil> imageUtils;
    private MediaImageAdapter imageAdapter;
    private boolean isList = false;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        initialiseRecyclerView();
        startGallery();

        LocalBroadcastManager.getInstance(this).registerReceiver(sortReceiver,
                new IntentFilter(BottomSheetDialog.IMAGE_TAG));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", imageUtils);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageUtils = savedInstanceState.getParcelableArrayList("list");
    }

    public void startGallery() {
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(LoaderManger_ID, null, this);
    }

    @NonNull
    @NotNull
    @Override
    public Loader<ArrayList<ImageUtil>> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Log.e(LOG_TAG, "started onLoadFinished");
        return new MediaImageLoader(getApplicationContext());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<ArrayList<ImageUtil>> loader, ArrayList<ImageUtil> data) {

        Log.e(LOG_TAG, "Done onLoadFinished");
        progressBar.setVisibility(View.GONE);
        if (imageUtils.isEmpty()) imageUtils.addAll(data);
        recyclerView.setVisibility(View.VISIBLE);
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<ArrayList<ImageUtil>> loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_menu, menu);
        return true;
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_by) {
            bottomSheetDialog = new BottomSheetDialog(this, BottomSheetDialog.IMAGE_TAG);
            bottomSheetDialog.show(getSupportFragmentManager(), "ModelBottomSheet");
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.list_grid) {
            RecyclerView.LayoutManager layoutManager;

            if (isList) {
                layoutManager = new GridLayoutManager(this, 2);
                item.setIcon(getDrawable(R.drawable.ic_baseline_view_list_24));
            } else {
                layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                item.setIcon(getDrawable(R.drawable.ic_baseline_view_grid_24));
            }
            recyclerView.setLayoutManager(layoutManager);
            isList = !isList;
            imageAdapter = new MediaImageAdapter(this, imageUtils, isList);
            recyclerView.setAdapter(imageAdapter);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialiseRecyclerView() {
        progressBar = findViewById(R.id.media_progress_bar);
        recyclerView = findViewById(R.id.media_recycler_view);
        recyclerView.setVisibility(View.GONE);
        imageUtils = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        if (isList) {
            RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        } else {
            RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        imageAdapter = new MediaImageAdapter(this, imageUtils, isList);
        recyclerView.setAdapter(imageAdapter);
    }

    public BroadcastReceiver sortReceiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            String tag = intent.getStringExtra(BottomSheetDialog.TAG);
            switch (tag) {
                case BottomSheetDialog.LARGEST_FIRST:
                    imageUtils.sort(Comparator.comparingInt(ImageUtil::getSize));
                    Collections.reverse(imageUtils);
                    break;
                case BottomSheetDialog.SMALLEST_FIRST:
                    imageUtils.sort(Comparator.comparingInt(ImageUtil::getSize));
                    break;
                case BottomSheetDialog.NEWEST_FIRST:
                    imageUtils.sort(Comparator.comparingLong(ImageUtil::getDate));
                    Collections.reverse(imageUtils);
                    break;
                case BottomSheetDialog.OLDEST_FIRST:
                    imageUtils.sort(Comparator.comparingLong(ImageUtil::getDate));
                    break;
                case BottomSheetDialog.A_TO_Z:
                    imageUtils.sort(Comparator.comparing(ImageUtil::getName));
                    break;
                case BottomSheetDialog.Z_TO_A:
                    imageUtils.sort(Comparator.comparing(ImageUtil::getName));
                    Collections.reverse(imageUtils);
                    break;
            }
            imageAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        }
    };

}