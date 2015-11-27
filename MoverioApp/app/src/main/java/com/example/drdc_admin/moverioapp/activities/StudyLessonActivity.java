package com.example.drdc_admin.moverioapp.activities;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.drdc_admin.moverioapp.R;
/**
 * Activity that displays study material (step / content) selected from LessonListActivity
 * 
 */
public class StudyLessonActivity extends AppCompatActivity {

    private Menu menu;
    private MediaController mediaControls;
    private int playPosition = 0;
    private int menuItemPosition = 1;
    private ProgressDialog progressDialog;
    private String path;
    private Bundle extras;
    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int STREAM_AUDIO = 2;
    private static final int RESOURCES_AUDIO = 3;
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

    private static final String TAG = "StudyLessonActivity";

    VideoView videoView;
    MediaController mc;
    Intent intent;
    Toolbar toolbar;

    /**
     *     called when LocalBroadcastManager (from MainActivity) sends something
     *     enables getting and handling messages when this activity is the current activity
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String myoGesture = intent.getStringExtra("gesture");
            Log.i(TAG, "Got message: " + myoGesture);

            handleGesture(context, myoGesture);

        }
    };

    /**
     * translate the given myo gesture and reflect it in this activity
     * the app takes different actions for the same gesture depending on
     * whether the option list is open or not
     * @param context
     * @param gesture myo gesture string sent from the phone
     */
    private void handleGesture(Context context, String gesture) {
        switch (gesture) {
            case "fingers spread":   //open or close options menu
                if (toolbar.isOverflowMenuShowing()) {
                    toolbar.hideOverflowMenu();
                } else {
                    toolbar.showOverflowMenu();
                }

//                http://stackoverflow.com/questions/13615229/android-programmatically-select-menu-option
                // http://stackoverflow.com/questions/3133318/how-to-open-the-options-menu-programmatically
                break;
            case "wave out":
                if (toolbar.isOverflowMenuShowing()) {
                    // move down the option list
                    moveUporDown("down");

                } else { // menu is not open
                    // go to prev / next item on the overflow menu
                }
                // play or go to next step
//                videoView.setVideoURI();
//                videoView.requestFocus();

                break;
            case "wave in":
                if (toolbar.isOverflowMenuShowing()) {
                    // move up the option list
                    moveUporDown("up");

                } else {
                    // go to prev / next item on the overflow menu

                }
                // if the user is not navigating options
                // play or prev step
                break;
            case "fist":     // pause or resume

                if (toolbar.isOverflowMenuShowing()) {

                } else {
                    if (videoView.isPlaying()) {
                        // pause the video
//                        Log.i(TAG, "Pause Video");
                        videoView.pause();
                        playPosition = videoView.getCurrentPosition();
//                    Log.i(TAG, "playPosition = " + playPosition);
                    } else {
                        // resume video
                        // http://examples.javacodegeeks.com/android/android-videoview-example/
                        videoView.seekTo(playPosition);
                        videoView.start();
//                        Log.i(TAG, "Resume Video");
                    }
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_studylesson, menu);

        // check the first menu item as default
        toolbar.getMenu().getItem(menuItemPosition).setCheckable(true);
        toolbar.getMenu().getItem(menuItemPosition).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_lesson);

        //TODO i dont know what to do
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TEST");
        setSupportActionBar(toolbar);

        intent = getIntent();
        String videoSource = intent.getStringExtra("filename");
        int videoRID = intent.getIntExtra("RID", 0);
        String uriPath = "android.resource://" + getPackageName() + "/" + videoRID;

        videoView = (VideoView) findViewById(R.id.videoView);
//        videoView.setVideoURI(Uri.parse(uriPath));
//        videoView.requestFocus();


        mediaControls = new MediaController(StudyLessonActivity.this);


//        // create a progress bar while the video file is loading
//        progressDialog = new ProgressDialog(StudyLessonActivity.this);
//        // set a title for the progress bar
//        progressDialog.setTitle("Wearables");
//        // set a message for the progress bar
//        progressDialog.setMessage("Loading...");
//        //set the progress bar not cancelable on users' touch
//        progressDialog.setCancelable(false);
//        // show the progress bar
//        progressDialog.show();

        try {
            // set the media controller in the VideoView
            videoView.setMediaController(mediaControls);
            //set the uri of the video to be played
            videoView.setVideoURI(Uri.parse(uriPath));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

//        videoView.requestFocus();
//        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                // close the progress bar and play the video
////                progressDialog.dismiss();
//                //if we have a playPosition on savedInstanceState, the video playback should start from here
//                videoView.seekTo(playPosition);
//                if (playPosition == 0) {
//                    videoView.start();
//                } else {
//                    //if we come from a resumed activity, video playback will be paused
//                    videoView.pause();
//                }
//            }
//        });


// http://www.java2s.com/Code/Android/Media/UsingMediaPlayertoplayVideoandAudio.htm
        videoView.requestFocus();
        videoView.start();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Proceed to the next lesson", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Deregister from BroadCastManager to prevent getting messages at unwanted times
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages only when this activity is the current activity.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("myo-event"));

    }

    /**
     * travel within the options menu
     * place the check mark beside the option item
     * @param upordown "up" or "down"
     */
    private void moveUporDown(String upordown) {

        // remove the checkmark
        menu.getItem(menuItemPosition).setCheckable(false);
        menu.getItem(menuItemPosition).setChecked(false);

        // update the position in the list
        if (upordown.equals("down")) {
            menuItemPosition++;
        }
        else if(upordown.equals("up")) {
            menuItemPosition--;
        }

        // reset if the position is too large or too small
        if (menuItemPosition > menu.size() - 1) {
            menuItemPosition = 1;
        }
        else if (menuItemPosition < 1) {
            menuItemPosition = menu.size() - 1;
            // item 0 is for Bluetootht
        }

        // put the check mark
        menu.getItem(menuItemPosition).setCheckable(true);
        menu.getItem(menuItemPosition).setChecked(true);

    }

}
