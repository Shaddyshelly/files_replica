package com.anjali.practice.filesbygooglereplica.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anjali.practice.filesbygooglereplica.R;
import com.anjali.practice.filesbygooglereplica.adapters.PdfAdapter;
import com.anjali.practice.filesbygooglereplica.loaders.PdfLoader;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ShowPdfActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Integer> {

    private static final int LoaderManger_ID = 40;
    private ProgressBar progressBar;
    private String uri;
    private ViewPager2 viewPager2;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf);

        Intent intent = getIntent();
        uri = intent.getStringExtra("pdf_uri");

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(100));

        progressBar = findViewById(R.id.progress_bar_pdf_activity);
        viewPager2 = findViewById(R.id.pdf_pager);
        error = findViewById(R.id.error);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(LoaderManger_ID, null, this);

    }

    @NonNull
    @NotNull
    @Override
    public Loader<Integer> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        viewPager2.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        return new PdfLoader(this, uri);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<Integer> loader, Integer data) {
        File file = new File(uri);
        if(data != -1){
            PdfAdapter adapter = new PdfAdapter(this, file.getAbsolutePath(), data);
            viewPager2.setAdapter(adapter);
            viewPager2.setVisibility(View.VISIBLE);
        }else{
            error.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<Integer> loader) { }

}