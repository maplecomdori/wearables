package com.example.drdc_admin.wearablephone.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.drdc_admin.wearablephone.R;

import java.io.IOException;

public class TutorialActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener
{

    MediaPlayer mp;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;
    String pathToContentFolder = null;
    private static String TAG = "TutorialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pathToContentFolder = Environment.getExternalStorageDirectory().getAbsolutePath();

        pathToContentFolder += "/airplane/tutorial.mp4";
        Log.i(TAG, pathToContentFolder);
        alert("In this tutorial, you will learn basic techniques to build a meccano piece.");
//        http://developer.android.com/guide/topics/media/mediaplayer.html
        vidSurface = (SurfaceView) findViewById(R.id.surfView);
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mp = new MediaPlayer();
            mp.setDisplay(vidHolder);
            mp.setDataSource(this, Uri.parse(pathToContentFolder));
            mp.prepare();
            mp.setOnPreparedListener(this);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    private void alert(String msg) {
        // alert the user to make a fist to go to
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle("Tutorial");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                mp.start();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
