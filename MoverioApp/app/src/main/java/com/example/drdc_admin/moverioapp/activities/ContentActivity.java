package com.example.drdc_admin.moverioapp.activities;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drdc_admin.moverioapp.Constants;
import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.fragments.stepImageFragment;
import com.example.drdc_admin.moverioapp.fragments.stepVideoFragment;
import com.example.drdc_admin.moverioapp.interfaces.Communicator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity that displays study material (step / content) selected from StepListActivity
 */
public class ContentActivity extends AppCompatActivity implements Communicator {

    private Menu menu;
    private int menuItemPosition = 0;

    public static String currentFileName; // without extension
    private static final String TAG = "ContentActivity";
    private static final String IMG_FRAG = "imgFragment";
    private static final String VIDEO_FRAG = "videoFragment";
    private static final int ALERT_SECONDS = 5 * 1000;
    //    public static String filename;
    private boolean isImageOn = false;
    public static String pathToContentFolder = null;
    private int numSteps;
    private TextView tv_gesture;
    private Intent intent;
    private Toolbar toolbar;
    private FragmentManager fManager;
    private stepImageFragment imgFragment;
    private stepVideoFragment videoFragment;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;
    /**
     * called when LocalBroadcastManager (from MainActivity) sends something
     * enables getting and handling messages when this activity is the current activity
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String myoGesture = intent.getStringExtra("gesture");
//            Log.i(TAG, "Got message: " + myoGesture);

            handleGesture(myoGesture);
//            if (isImageOn) {
//                imgFragment.handleGesture(myoGesture);
//
//            } else {
//                videoFragment.handleGesture(myoGesture);
//            }
        }
    };

    @Override
    public void openMenu() {
        if (!toolbar.isOverflowMenuShowing()) {
            toolbar.showOverflowMenu();
        }

    }

    @Override
    public void closeMenu() {
        if (toolbar.isOverflowMenuShowing()) {
            toolbar.hideOverflowMenu();
        }
    }


    public void handleFingersSpread() {
        if (toolbar.isOverflowMenuShowing()) {
            toolbar.hideOverflowMenu();
        } else if (!toolbar.isOverflowMenuShowing()) {
            toolbar.showOverflowMenu();
        }

    }

    /**
     * travel within the options menu
     * place the check mark beside the option item
     *
     * @param upordown "up" or "down"
     */

    @Override
    public void moveUporDown(String upordown) {

        // remove the checkmark
        menu.getItem(menuItemPosition).setChecked(false);

        // update the position in the list
        if (upordown.equals(Constants.DOWNMENU)) {
            menuItemPosition++;
        } else if (upordown.equals(Constants.UPMENU)) {
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

    /**
     * translate the given myo gesture and reflect it in this activity
     * the app takes different actions for the same gesture depending on
     * whether the option list is open or not
     *
     * @param gesture myo gesture string sent from the phone
     */
    @Override
    public void handleGesture(String gesture) {

        if (toolbar.isOverflowMenuShowing()) {

            switch (gesture) {
                case Constants.MYO_FINGERSPEREAD:
//                    handleFingersSpread();
                    closeMenu();
                    if(!isImageOn) { // resume video
                        videoFragment.play();
                    }

                    break;
                case Constants.MYO_WAVEOUT:
                        moveUporDown(Constants.DOWNMENU);

                    break;

                case Constants.MYO_WAVEIN:

                        moveUporDown(Constants.UPMENU);
                    break;
                case Constants.MYO_FIST:
                    // choose the current selections

                        onOptionsItemSelected(menu.getItem(menuItemPosition));
                        closeMenu();
                    break;
            }
        }
        else {

            if (isImageOn) {
                imgFragment.handleGesture(gesture);
            } else {
                videoFragment.handleGesture(gesture);
            }
        }
//        switch (gesture) {
//            case Constants.MYO_FINGERSPEREAD:
//                handleFingersSpread();
//
//                if (isImageOn) {
//                    imgFragment.handleGesture(gesture);
//                } else {
//                    videoFragment.handleGesture(gesture);
//                }
//
//                break;
//            case Constants.MYO_WAVEOUT:
//
//                if (toolbar.isOverflowMenuShowing()) {
//                    moveUporDown(Constants.DOWNMENU);
//                } else {
//                    if (isImageOn) {
//                        imgFragment.handleGesture(gesture);
//                    } else {
//                        videoFragment.handleGesture(gesture);
//                    }
//                }
//
//                break;
//
//            case Constants.MYO_WAVEIN:
//
//                if (toolbar.isOverflowMenuShowing()) {
//                    moveUporDown(Constants.UPMENU);
//                } else {
//                    if (isImageOn) {
//                        imgFragment.handleGesture(gesture);
//                    } else {
//                        videoFragment.handleGesture(gesture);
//                    }
//                }
//
//                break;
//            case Constants.MYO_FIST:
//                // choose the current selections
//                if (toolbar.isOverflowMenuShowing()) {
//
//                    onOptionsItemSelected(menu.getItem(menuItemPosition));
//                    closeMenu();
//                } else {
//                    if (isImageOn) {
//                        imgFragment.handleGesture(gesture);
//                    } else {
//                        videoFragment.handleGesture(gesture);
//                    }
//                }
//
//                break;
//        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        tv_gesture = (TextView) findViewById(R.id.gestureOnVideo);


        // set the path to the content folder depending on the device used "rooted/unrooted galaxy & moverio"
        // Returns the absolute path to the directory on the filesystem where files created with openFileOutput(String, int) are stored.
        getFilesDir();

        // Return the primary shared/external storage directory.
        pathToContentFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
        // TODO add specific folder name to pathToContentFolder (ex airpalne)
//        Log.i(TAG, "pathToContentFolder =" + pathToContentFolder);

        pathToContentFolder += "/airplane/";

        // extract the filename and R.id for the video the user selected
        intent = getIntent();
        String jsonString = intent.getStringExtra(Constants.JSON_STRING);

        try {
            JSONObject json = new JSONObject(jsonString);
            currentFileName = json.getString(Constants.VIDEO_FILENAME);

            // TODO get the information about the current step such as title description
        } catch (JSONException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.studyStepToolbar);
        updateToolbarTitle();
        setSupportActionBar(toolbar);

        imgFragment = stepImageFragment.newInstance(currentFileName);
        videoFragment = stepVideoFragment.newInstance(currentFileName);

        alert("Fist -> Switch to video \n\nFinger-spread -> Open Menu Option");

        // test turn on screen
        // #1
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = 1;
        getWindow().setAttributes(params);

        putImgFragment();


    }


    private void alert(String msg) {
        // alert the user to make a fist to go to
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(R.string.dialogueMsg);
        builder.setMessage(msg);
        builder.setTitle("Information");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        new CountDownTimer(ALERT_SECONDS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
            }

            @Override
            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();
        // dismiss after some time

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;

        // add 'replay' option only when video is on
        if (isImageOn) {
            // remove 'replay' option when image is on
            Log.e(TAG, "onPrepareOptionsMenu isImageon = TRUE");
            menu.removeItem(R.id.replay);
        } else {
            if (menu.findItem(R.id.replay) == null) {
                menu.add(Menu.NONE, R.id.replay, Menu.NONE, "Replay");
                menu.getItem(menu.size() - 1).setCheckable(true);
            }

        }

        return true;
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
                if (!isImageOn) {
                    videoFragment.replay();
                    closeMenu();
                }
                break;
            case R.id.nextStep:
                goToNextStep();
                toolbar.hideOverflowMenu();
                break;
            case R.id.prevStep:
                goToPrevStep();
                toolbar.hideOverflowMenu();
                break;
        }
        return false;
    }

    @Override
    public void putVideoFragment() {

        videoFragment.getArguments().putString("fileNameWithoutExt", currentFileName);
        fManager = getFragmentManager();
        FragmentTransaction transaction = fManager.beginTransaction();
        transaction.replace(R.id.contentActivity, videoFragment, VIDEO_FRAG);
        transaction.commit();
        isImageOn = false;
        updateToolbarTitle();

    }

    @Override
    public void putImgFragment() {

        if (isImageOn) {
            updateToolbarTitle();
            imgFragment.setImage(currentFileName);
            imgFragment.setText(currentFileName);
            imgFragment.getArguments().putString("fileNameWithoutExt", currentFileName);
        } else { //video is on
//        Log.i(TAG, imgFragment.getArguments().getString("fileNameWithoutExt"));
            imgFragment.getArguments().putString("fileNameWithoutExt", currentFileName);
//        Log.i(TAG, imgFragment.getArguments().getString("fileNameWithoutExt"));
            fManager = getFragmentManager();
            FragmentTransaction transaction = fManager.beginTransaction();
            transaction.replace(R.id.contentActivity, imgFragment, IMG_FRAG);
            transaction.commit();
            isImageOn = true;
            updateToolbarTitle();
        }

    }

    public void goToNextStep() {
//        Log.i(TAG, "currentFileName = " + currentFileName);

        // extract nondigits from the file
        int currentStepNum = Integer.parseInt(currentFileName.replaceAll("[^0-9]", ""));
//        Log.i(TAG, "currentStepNum = " + currentStepNum);

        // change "airplanestep01" to "airplanestep02"
        String chars = currentFileName.replaceAll("[0-9]", "");
        int newStep = currentStepNum + 1;

        // TODO check if newStep > total num of steps

        String newFileName;
        if (newStep < 10) {
            newFileName = chars + 0 + newStep;
        } else {
            newFileName = chars + newStep;
        }

        // update the variable currentFileName
        currentFileName = newFileName;

        putImgFragment();
    }

    public void goToPrevStep() {
//        Log.i(TAG, "currentFileName = " + currentFileName);

        // extract nondigits from the file
        int currentStepNum = Integer.parseInt(currentFileName.replaceAll("[^0-9]", ""));
//        Log.i(TAG, "currentStepNum = " + currentStepNum);

        // change "airplanestep01" to "airplanestep02"
        String chars = currentFileName.replaceAll("[0-9]", "");
        int newStep = currentStepNum - 1;
        if (newStep < 1) {
            newStep = 1;
            Toast.makeText(this, "You are at the first step", Toast.LENGTH_SHORT).show();
        }

        String newFileName;
        if (newStep < 10) {
            newFileName = chars + 0 + newStep;
        } else {
            newFileName = chars + newStep;
        }
        // update the variable currentFileName
        currentFileName = newFileName;

        putImgFragment();


    }

    private void updateToolbarTitle() {
        int currentStepNum = Integer.parseInt(currentFileName.replaceAll("[^0-9]", ""));
        toolbar.setTitle("Step #" + currentStepNum);
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
}
