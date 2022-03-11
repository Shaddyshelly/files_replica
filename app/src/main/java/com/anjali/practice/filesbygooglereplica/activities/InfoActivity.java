package com.anjali.practice.filesbygooglereplica.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Size;
import android.widget.ImageView;
import android.widget.TextView;

import com.anjali.practice.filesbygooglereplica.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

public class InfoActivity extends AppCompatActivity {

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageView image = findViewById(R.id.info_activity_image);
        TextView name = findViewById(R.id.image_name);
        TextView location = findViewById(R.id.image_location);
        TextView time = findViewById(R.id.image_date);
        TextView size = findViewById(R.id.info_image_size);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("INFO");

        String uri_string = bundle.getString("uri");
        String name_string = bundle.getString("name");
        String location_string = bundle.getString("location");
        String time_string = bundle.getString("time");
        String size_string = bundle.getString("size");
        int mediaType = bundle.getInt("isMedia");

        /*
        media image = 0;
        media audio = 1;
        media video = 2;
        media document = 3;
        internal image = 4;
        internal audio = 5;
        internal video = 6;
        * */

        switch (mediaType) {
            case 0:
            case 2:
                Glide.with(this)
                        .load(Uri.parse(uri_string))
                        .into(image);
                break;
            case 1:
                try {
                    Bitmap bitmap = getContentResolver().loadThumbnail(
                            Uri.parse(uri_string),
                            new Size(200, 200),
                            null
                    );
                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    image.setImageDrawable(getDrawable(R.drawable.ic_baseline_audiotrack_24));
                }
                break;
            case 4:
                image.setImageURI(Uri.parse(uri_string));
                break;
            case 5:
                try {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(new File(uri_string),
                            new Size(200, 200), null);
                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    image.setImageDrawable(getDrawable(R.drawable.ic_baseline_audiotrack_24));
                }
            case 6:
                try {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(new File(uri_string),
                            new Size(200, 200), null);
                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    image.setImageDrawable(getDrawable(R.drawable.ic_baseline_videocam_24));
                }
                break;

        }

        name.setText(name_string);
        location.setText(location_string + name_string);
        time.setText(time_string);
        size.setText(size_string);
    }
}