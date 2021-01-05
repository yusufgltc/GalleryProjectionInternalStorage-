package com.example.quickvideoplayer;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class Activity_Videos extends AppCompatActivity {

    private ArrayList<ModelVideo> videosList = new ArrayList<ModelVideo>();
    private AdapterVideoList adapterVideoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        initializeViews();
        //checkPermissions();
    }

    private void initializeViews() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_videos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); //3 = column count
        adapterVideoList = new AdapterVideoList(this, videosList);
        recyclerView.setAdapter(adapterVideoList);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            } else {
                loadVideos2();
            }
        } else {
            loadVideos2();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideos2();
            } else {
                Toast.makeText(this, "Permission was not granteddd", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadVideos() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION};

                Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                        MediaStore.Video.Media.DATA +" like ? ",
                        new String[]{"%Camera%"},
                        null);

                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        int duration = cursor.getInt(durationColumn);

                        Uri data = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                        String duration_formatted;
                        int sec = (duration / 1000) % 60;
                        int min = (duration / (1000 * 60)) % 60;
                        int hrs = duration / (1000 * 60 * 60);

                        if (hrs == 0) {
                            duration_formatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                        } else {    
                            duration_formatted = String.valueOf(hrs).concat(":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                        }

                        videosList.add(new ModelVideo(id, data, title, duration_formatted));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapterVideoList.notifyItemInserted(videosList.size() - 1);
                            }
                        });
                    }
                }

            }
        }.start();
    }

    private void loadVideos2() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String FILENAME = "mooddylooo.mp4";
                String FOLDERNAME = "&Moody&";
                byte[] bytes = new byte[1024];

                Context context = getApplicationContext();
                String folder = context.getFilesDir().getAbsolutePath() + File.separator + "Moody";

                String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION};

                Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, projection,
                        MediaStore.MediaColumns.DATA +" like ? ",
                        new String[]{"%Moody%"},
                        null);

                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        int duration = cursor.getInt(durationColumn);

                        Uri data = ContentUris.withAppendedId(MediaStore.Video.Media.INTERNAL_CONTENT_URI, id);

                        String duration_formatted;
                        int sec = (duration / 1000) % 60;
                        int min = (duration / (1000 * 60)) % 60;
                        int hrs = duration / (1000 * 60 * 60);

                        if (hrs == 0) {
                            duration_formatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                        } else {
                            duration_formatted = String.valueOf(hrs).concat(":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                        }

                        videosList.add(new ModelVideo(id, data, title, duration_formatted));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapterVideoList.notifyItemInserted(videosList.size() - 1);
                            }
                        });
                    }
                }

            }
        }.start();
    }

}
