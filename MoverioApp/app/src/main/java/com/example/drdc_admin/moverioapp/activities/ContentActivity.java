package com.example.drdc_admin.moverioapp.activities;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.drdc_admin.moverioapp.R;
/**
 * Activity that displays study material (step / content) selected from StepListActivity
 * 
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

    private static final String TAG = "ContentActivity";

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

        // position in the video
        int tmpPosition = videoView.getCurrentPosition();
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

//                http://stackoverflow.com/questions/13615229/android-programmatically-select-menu-option
                // http://stackoverflow.com/questions/3133318/how-to-open-the-options-menu-programmatically
                break;
            case "wave out":
                if (toolbar.isOverflowMenuShowing()) {
                    // move down the option list
                    moveUporDown("down");

                } else { // menu is not open
                    // rewind 5 seconds
                    videoView.seekTo(tmpPosition - (5 * 1000));

                }

                break;
            case "wave in":
                if (toolbar.isOverflowMenuShowing()) {
                    // move up the option list
                    moveUporDown("up");

                } else {
                    // fast forward 5 sec
                    videoView.seekTo(tmpPosition + (5 * 1000));

                }
                // if the user is not navigating options
                // play or prev step
                break;
            case "fist":     // pause or resume

                if (toolbar.isOverflowMenuShowing()) {
                    // take actions

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
                        // videoView.seekTo(playPosition);
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
        getMenuInflater().inflate(R.menu.menu_content_activity, menu);

        for (int i = 0; i < menu.size(); i ++) {
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
                videoView.seekTo(0);
                videoView.start();
                break;
            case R.id.goBack:
                finish();
                break;
            case R.id.nextStep:

                break;

            case R.id.prevStep:

                break;

        }
        return false;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        //TODO i dont know what to do
        toolbar = (Toolbar) findViewById(R.id.studyStepToolbar);
        toolbar.setTitle("Step ###");
        setSupportActionBar(toolbar);

        intent = getIntent();
        String videoSource = intent.getStringExtra("filename");
        int videoRID = intent.getIntExtra("RID", 0);
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
/*


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Proceed to the next lesson", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
 */
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
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p/>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {



        // menu.getItem(menuItemPosition).setCheckable(true);
        menu.getItem(menuItemPosition).setChecked(true);

        return true;
    }

    /**
     * travel within the options menu
     * place the check mark beside the option item
     * @param upordown "up" or "down"
     */
    private void moveUporDown(String upordown) {

        // remove the checkmark
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
            menuItemPosition = 0;
        }
        else if (menuItemPosition < 0) {
            menuItemPosition = menu.size() - 1;
            // item 0 is for Bluetoothtet
        }

        // put the check mark
//        invalidateOptionsMenu();
        menu.getItem(menuItemPosition).setChecked(true);

    }

}
