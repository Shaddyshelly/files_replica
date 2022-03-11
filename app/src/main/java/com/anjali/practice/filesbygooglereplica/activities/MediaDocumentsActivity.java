package com.anjali.practice.filesbygooglereplica.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.anjali.practice.filesbygooglereplica.utilities.BottomSheetDialog;
import com.anjali.practice.filesbygooglereplica.models.DocumentsUtil;
import com.anjali.practice.filesbygooglereplica.adapters.MediaDocAdapter;
import com.anjali.practice.filesbygooglereplica.loaders.MediaDocLoader;
import com.anjali.practice.filesbygooglereplica.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MediaDocumentsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<DocumentsUtil>> {

    private final String LOG_TAG = MediaDocumentsActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private static final int LoaderManger_ID = 25;
    private ArrayList<DocumentsUtil> documentsUtils;
    private MediaDocAdapter mediaDocAdapter;
    private boolean isList = false;
    private BottomSheetDialog bottomSheetDialog;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        initialiseRecyclerView();
        showDocuments();
        LocalBroadcastManager.getInstance(this).registerReceiver(sortReceiver,
                new IntentFilter(BottomSheetDialog.DOCUMENT_TAG));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", documentsUtils);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        documentsUtils = savedInstanceState.getParcelableArrayList("list");
    }

    private void showDocuments() {
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(LoaderManger_ID, null, this);
    }

    @NonNull
    @NotNull
    @Override
    public Loader<ArrayList<DocumentsUtil>> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Log.e(LOG_TAG, "started onLoadFinished");
        return new MediaDocLoader(getApplicationContext());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<ArrayList<DocumentsUtil>> loader, ArrayList<DocumentsUtil> data) {

        progressBar.setVisibility(View.GONE);
        Log.e(LOG_TAG, "Done onLoadFinished");
        if(documentsUtils.isEmpty()) documentsUtils.addAll(data);
        recyclerView.setVisibility(View.VISIBLE);
        mediaDocAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<ArrayList<DocumentsUtil>> loader) {
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
            bottomSheetDialog = new BottomSheetDialog(this, BottomSheetDialog.DOCUMENT_TAG);
            bottomSheetDialog.show(getSupportFragmentManager(), "ModelBottomSheet");
            return true;
        }  else if (item.getItemId() == R.id.list_grid) {
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
            mediaDocAdapter = new MediaDocAdapter(this, documentsUtils, isList);
            recyclerView.setAdapter(mediaDocAdapter);

            return true;
        }else if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialiseRecyclerView() {
        progressBar = findViewById(R.id.media_progress_bar);
        recyclerView = findViewById(R.id.media_recycler_view);
        recyclerView.setVisibility(View.GONE);
        documentsUtils = new ArrayList<>();
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
        mediaDocAdapter = new MediaDocAdapter(this, documentsUtils, isList);
        recyclerView.setAdapter(mediaDocAdapter);
    }

    public BroadcastReceiver sortReceiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            String tag = intent.getStringExtra(BottomSheetDialog.TAG);
            switch (tag) {
                case BottomSheetDialog.LARGEST_FIRST:
                    documentsUtils.sort(Comparator.comparingInt(DocumentsUtil::getSize));
                    Collections.reverse(documentsUtils);
                    break;
                case BottomSheetDialog.SMALLEST_FIRST:
                    documentsUtils.sort(Comparator.comparingInt(DocumentsUtil::getSize));
                    break;
                case BottomSheetDialog.NEWEST_FIRST:
                    documentsUtils.sort(Comparator.comparingLong(DocumentsUtil::getDate));
                    Collections.reverse(documentsUtils);
                    break;
                case BottomSheetDialog.OLDEST_FIRST:
                    documentsUtils.sort(Comparator.comparingLong(DocumentsUtil::getDate));
                    break;
                case BottomSheetDialog.A_TO_Z:
                    documentsUtils.sort(Comparator.comparing(DocumentsUtil::getName));
                    break;
                case BottomSheetDialog.Z_TO_A:
                    documentsUtils.sort(Comparator.comparing(DocumentsUtil::getName));
                    Collections.reverse(documentsUtils);
                    break;
            }
            mediaDocAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        }
    };

}