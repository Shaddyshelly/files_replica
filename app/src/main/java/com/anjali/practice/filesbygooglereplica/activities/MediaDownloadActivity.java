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
import com.anjali.practice.filesbygooglereplica.models.DownloadUtils;
import com.anjali.practice.filesbygooglereplica.adapters.MediaDownloadAdapter;
import com.anjali.practice.filesbygooglereplica.loaders.MediaDownloadLoader;
import com.anjali.practice.filesbygooglereplica.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MediaDownloadActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<DownloadUtils>> {

    private final String LOG_TAG = MediaDownloadActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private static final int LoaderManger_ID = 30;
    private MediaDownloadAdapter downloadAdapter;
    private ArrayList<DownloadUtils> downloadUtils;
    private boolean isList = false;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        initialiseRecyclerView();
        showDownloads();
        LocalBroadcastManager.getInstance(this).registerReceiver(sortReceiver,
                new IntentFilter(BottomSheetDialog.DOWNLOAD_TAG));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", downloadUtils);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        downloadUtils = savedInstanceState.getParcelableArrayList("list");
    }

    private void showDownloads(){
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(LoaderManger_ID, null, this);
    }

    @NonNull
    @NotNull
    @Override
    public Loader<ArrayList<DownloadUtils>> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Log.e(LOG_TAG, "started onLoadFinished");
        return new MediaDownloadLoader(getApplicationContext());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<ArrayList<DownloadUtils>> loader, ArrayList<DownloadUtils> data) {

        Log.e(LOG_TAG, "Done onLoadFinished");
        progressBar.setVisibility(View.GONE);
        if(downloadUtils.isEmpty()) downloadUtils.addAll(data);
        recyclerView.setVisibility(View.VISIBLE);
        downloadAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<ArrayList<DownloadUtils>> loader) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_menu, menu);
        return true;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_by) {
            bottomSheetDialog = new BottomSheetDialog(this, BottomSheetDialog.DOWNLOAD_TAG);
            bottomSheetDialog.show(getSupportFragmentManager(), "ModelBottomSheet");
            return true;
        } else if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else if(item.getItemId() == R.id.list_grid){
            RecyclerView.LayoutManager layoutManager;

            if(isList){
                layoutManager = new GridLayoutManager(this, 2);
                item.setIcon(getDrawable(R.drawable.ic_baseline_view_list_24));
            }else{
                layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                item.setIcon(getDrawable(R.drawable.ic_baseline_view_grid_24));
            }
            recyclerView.setLayoutManager(layoutManager);
            isList = !isList;
            downloadAdapter = new MediaDownloadAdapter(this, downloadUtils, isList);
            recyclerView.setAdapter(downloadAdapter);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialiseRecyclerView(){
        progressBar = findViewById(R.id.media_progress_bar);
        recyclerView = findViewById(R.id.media_recycler_view);
        recyclerView.setVisibility(View.GONE);
        downloadUtils = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        if(isList){
            RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        }else{
            RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        downloadAdapter = new MediaDownloadAdapter(this, downloadUtils, isList);
        recyclerView.setAdapter(downloadAdapter);
    }

    public BroadcastReceiver sortReceiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            String tag = intent.getStringExtra(BottomSheetDialog.TAG);
            switch (tag) {
                case BottomSheetDialog.LARGEST_FIRST:
                    downloadUtils.sort(Comparator.comparingInt(DownloadUtils::getSize));
                    Collections.reverse(downloadUtils);
                    break;
                case BottomSheetDialog.SMALLEST_FIRST:
                    downloadUtils.sort(Comparator.comparingInt(DownloadUtils::getSize));
                    break;
                case BottomSheetDialog.NEWEST_FIRST:
                    downloadUtils.sort(Comparator.comparingLong(DownloadUtils::getDate));
                    Collections.reverse(downloadUtils);
                    break;
                case BottomSheetDialog.OLDEST_FIRST:
                    downloadUtils.sort(Comparator.comparingLong(DownloadUtils::getDate));
                    break;
                case BottomSheetDialog.A_TO_Z:
                    downloadUtils.sort(Comparator.comparing(DownloadUtils::getName));
                    break;
                case BottomSheetDialog.Z_TO_A:
                    downloadUtils.sort(Comparator.comparing(DownloadUtils::getName));
                    Collections.reverse(downloadUtils);
                    break;
            }
            downloadAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        }
    };

}