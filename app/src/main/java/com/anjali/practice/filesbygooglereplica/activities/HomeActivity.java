package com.anjali.practice.filesbygooglereplica.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anjali.practice.filesbygooglereplica.R;

public class HomeActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_CODE = 5;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ActivityResultLauncher<Intent> activityResultLauncher;
    LinearLayout download, images, videos, music, documents, internalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    if(Environment.isExternalStorageManager()) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                        show();
                    } else Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            checkPermissionsStatus();
        }else{
            if(!checkPermissionStatus()){
                requestPermission();
            }else show();
        }
    }

    private void show(){
        download = findViewById(R.id.download_layout);
        images = findViewById(R.id.image_layout);
        videos = findViewById(R.id.video_layout);
        music = findViewById(R.id.audio_layout);
        documents = findViewById(R.id.document_layout);
        internalStorage = findViewById(R.id.internal_storage_layout);

        internalStorage.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, InternalStorageActivity.class);
            startActivity(intent);
        });

        download.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MediaDownloadActivity.class);
            startActivity(intent);
        });

        images.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MediaImageActivity.class);
            startActivity(intent);
        });

        videos.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MediaVideoActivity.class);
            startActivity(intent);
        });

        music.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MediaAudioActivity.class);
            startActivity(intent);
        });

        documents.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MediaDocumentsActivity.class);
            startActivity(intent);
        });

    }

    private boolean checkPermissionStatus(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else{
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            new AlertDialog.Builder(this)
                    .setTitle("Request Permission!!")
                    .setMessage("You have to give access to permission so that the app can show files")
                    .setPositiveButton("OK", (dialog, which) -> {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                            activityResultLauncher.launch(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            activityResultLauncher.launch(intent);
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            if (grantResults.length > 0) {
//                boolean readPer = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                boolean writePer = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                if (readPer && writePer) {
//                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                    show();
//                }else
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "You denied Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    //    list of permissions required
    @SuppressLint("InlinedApi")
    String[] PERMISSION_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    //  Method for permission handling
    private void checkPermissionsStatus() {

//      check weather the permission is given or not
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            show();
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
//                if not permission is denied than show the message to user showing him the benefits of the permission.
                Toast.makeText(this, "Permission is needed to show the Media..", Toast.LENGTH_SHORT).show();
                finish();
            }
//            request permission
            requestPermissions(PERMISSION_LIST, REQUEST_PERMISSION_CODE);
        }
    }

    //    handling the permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                show();
            } else{
                Toast.makeText(this,"Permissions not granted!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}