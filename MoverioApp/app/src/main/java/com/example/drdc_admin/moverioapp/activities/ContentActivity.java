package com.example.drdc_admin.moverioapp.activities;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.drdc_admin.moverioapp.Constants;
import com.example.drdc_admin.moverioapp.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity that displays study material (step / content) selected from StepListActivity
 */
public class ContentActivity extends AppCompatActivity {

    private Menu menu;
    private MediaController mediaControls;
    private static int playPosition = 0;
    private int menuItemPosition = 0;
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

    private String videoFileName;
    private static final String TAG = "ContentActivity";

    private TextView tv_gesture;
    private VideoView videoView;
    private MediaController mc;
    private Intent intent;
    private Toolbar toolbar;
    private long timeNewGesture;

    /**
     * called when LocalBroadcastManager (from MainActivity) sends something
     * enables getting and handling messages when this activity is the current activity
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String myoGesture = intent.getStringExtra("gesture");
//            Log.i(TAG, "Got message: " + myoGesture);

            handleGesture(context, myoGesture);
        }
    };

    /**
     * translate the given myo gesture and reflect it in this activity
     * the app takes different actions for the same gesture depending on
     * whether the option list is open or not
     *
     * @param context
     * @param gesture myo gesture string sent from the phone
     */
    private void handleGesture(Context context, String gesture) {

        // inform the user which gesture has been made except "rest"
        if (!gesture.equals("rest")) {

            // inform the user which gesture has been recognized
            timeNewGesture = System.currentTimeMillis();
            tv_gesture.setText(gesture);
            tv_gesture.setVisibility(View.VISIBLE);

            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // fade out image?
                }

                @Override
                public void onFinish() {
                    long now = System.currentTimeMillis();
                    // hide textview if the user did not make any gesture in the last x second
                    if ((now - timeNewGesture) >= (1 * 1000)) {
                        tv_gesture.setVisibility(View.INVISIBLE);
                    }
                }
            }.start();
        }

        // position in the video
        int currentPosition = videoView.getCurrentPosition();
        switch (gesture) {
            case "fingers spread":   //open or close options menu
                if (toolbar.isOverflowMenuShowing()) {
                    toolbar.hideOverflowMenu();
                    videoView.seekTo(playPosition);
                    videoView.start();
                } else {
                    videoView.pause();
                    playPosition = videoView.getCurrentPosition();
                    toolbar.showOverflowMenu();
                }
                Log.e(TAG, "playPosition = " + playPosition);
//                http://stackoverflow.com/questions/13615229/android-programmatically-select-menu-option
                // http://stackoverflow.com/questions/3133318/how-to-open-the-options-menu-programmatically
                break;
            case "wave out":
                if (toolbar.isOverflowMenuShowing()) {
                    // move down the option list
                    moveUporDown("down");
                } else { // menu is not open
                    // rewind 5 seconds
                    playPosition = currentPosition - (5 * 1000);
                    if (playPosition < 0)
                        playPosition = 0;
                    videoView.seekTo(playPosition);
                }
                Log.e(TAG, "playPosition = " + playPosition);
                break;
            case "wave in":
                if (toolbar.isOverflowMenuShowing()) {
                    // move up the option list
                    moveUporDown("up");
                } else {
                    // fast forward 5 sec
                    playPosition = currentPosition + (5 * 1000);
                    videoView.seekTo(playPosition);
                }
                Log.e(TAG, "playPosition = " + playPosition);
                break;
            case "fist":     // pause or resume

                if (toolbar.isOverflowMenuShowing()) {
                    // take actions
                    onOptionsItemSelected(menu.getItem(menuItemPosition));

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
                    }
                }
                Log.e(TAG, "playPosition = " + playPosition);
                break;
        }
    }

    /**
     * play the next or prev video
     *
     * @param nextOrPrev "next" or "prev"
     */
    private void playStep(String nextOrPrev) {
        Log.i(TAG, "videoFileName = " + videoFileName);

        // extract nondigits from the file
        int currentStep = Integer.parseInt(videoFileName.replaceAll("[^0-9]", ""));
        Log.i(TAG, "currentStep = " + currentStep);

        // change airplanestep1 to airplanestep2
        String chars = videoFileName.replaceAll("[0-9]", "");
        int newStep = 0;
        if (nextOrPrev.equals("next")) {
            newStep = currentStep + 1;
        } else if (nextOrPrev.equals("prev")) {

            newStep = currentStep - 1;
            if (newStep < 1) {
                newStep = 1;
            }
        }
        String newFileName = chars + newStep;
        Log.i(TAG, "chars = " + chars);
        Log.i(TAG, "newStep = " + newStep);
        Log.i(TAG, "newFileName = " + newFileName);

        // update the variable videoFileName
        videoFileName = newFileName;

        // check if it's airplane or heli and change directory

        // load the next video
        //TODO remove buffering
        videoView.setVideoPath("/storage/sdcard1/airplane/" + videoFileName + ".mp4");

        videoView.start();

        toolbar.hideOverflowMenu();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        tv_gesture = (TextView) findViewById(R.id.gesture);

        toolbar = (Toolbar) findViewById(R.id.studyStepToolbar);
        toolbar.setTitle("Step ###");
        setSupportActionBar(toolbar);

        // extract the filename and R.id for the video the user selected
        intent = getIntent();
        String jsonString = intent.getStringExtra(Constants.JSON_STRING);
        try {
            JSONObject json = new JSONObject(jsonString);
            videoFileName = json.getString(Constants.VIDEO_FILENAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        videoFileName = intent.getStringExtra(Constants.VIDEO_FILENAME);
        Log.i(TAG, "videoFileName = " + videoFileName);
        int videoRID = intent.getIntExtra(Constants.VIDEO_RID, 0);
        String uriPath = "android.resource://" + getPackageName() + "/" + videoRID;

        videoView = (VideoView) findViewById(R.id.videoView);
//        videoView.setVideoURI(Uri.parse(uriPath));
//        videoView.requestFocus();

        mediaControls = new MediaController(ContentActivity.this);

//        // create a progress bar while the video file is loading
//        progressDialog = new ProgressDialog(ContentActivity.this);
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
            // set the uri of the video to be played in the internal storage
            // videoView.setVideoURI(Uri.parse(uriPath));

            // play video in the sd card
            videoView.setVideoPath("/storage/sdcard1/airplane/" + videoFileName + ".mp4");
//            videoView.setVideoPath(Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    "airplanestep1.mp4");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();

        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                // close the progress bar and play the video
//                progressDialog.dismiss();
                //if we have a playPosition on savedInstanceState, the video playback should start from here
                videoView.seekTo(playPosition);
                if (playPosition == 0) {
                    videoView.start();
                } else {
                    //if we come from a resumed activity, video playback will be paused
                    videoView.pause();
                }
            }
        });


// http://www.java2s.com/Code/Android/Media/UsingMediaPlayertoplayVideoandAudio.htm
        // videoView.start();

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
     *
     * @param upordown "up" or "down"
     */
    private void moveUporDown(String upordown) {

        // remove the checkmark
        menu.getItem(menuItemPosition).setChecked(false);

        // update the position in the list
        if (upordown.equals("down")) {
            menuItemPosition++;
        } else if (upordown.equals("up")) {
            menuItemPosition--;
        }

        // reset if the position is too large or too small
        if (menuItemPosition > menu.size() - 1) {
            menuItemPosition = 0;
        } else if (menuItemPosition < 0) {
            menuItemPosition = menu.size() - 1;
            // item 0 is for Bluetoothtet
        }

        // put the check mark
//        invalidateOptionsMenu();
        menu.getItem(menuItemPosition).setChecked(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content_activity, menu);

        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setCheckable(true);
        }
        // check the first menu item as default
        toolbar.getMenu().getItem(menuItemPosition).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.replay:
                playPosition = 0;
                videoView.seekTo(0);
                videoView.start();
                break;
            case R.id.goBack:
                finish();
                break;
            case R.id.nextStep:
                playStep("next");
                playPosition = 0;
                break;

            case R.id.prevStep:
                playStep("prev");
                playPosition = 0;
                break;
        }
        return false;
    }
}
