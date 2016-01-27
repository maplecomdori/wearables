package com.example.drdc_admin.moverioapp.activities;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.drdc_admin.moverioapp.Constants;
import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.fragments.stepImageFragment;
import com.example.drdc_admin.moverioapp.fragments.stepVideoFragment;
import com.example.drdc_admin.moverioapp.interfaces.Communicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
    private boolean isImageOn;
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

            handleGesture(context, myoGesture);
        }
    };

    @Override
    public void openMenu() {
        toolbar.showOverflowMenu();
    }

    @Override
    public void closeMenu() {
        toolbar.hideOverflowMenu();
    }

    /**
     * translate the given myo gesture and reflect it in this activity
     * the app takes different actions for the same gesture depending on
     * whether the option list is open or not
     *
     * @param context
     * @param gesture myo gesture string sent from the phone
     */
    private void handleGesture(Context context, String gesture) {

        if (toolbar.isOverflowMenuShowing()) {

            switch (gesture) {
                case Constants.MYO_FINGERSPEREAD:
                    toolbar.hideOverflowMenu();
                    break;
                case Constants.MYO_WAVEOUT:
                    moveUporDown("down");
                    break;
                case Constants.MYO_WAVEIN:
                    moveUporDown("up");
                    break;
                case Constants.MYO_FIST:
                    // choose the current selections
                    onOptionsItemSelected(menu.getItem(menuItemPosition));
                    break;
            }

        } else { //OverflowMenu is hidden

            if (isImageOn) {
                imgFragment.handleGesture(gesture);

            } else {
                videoFragment.handleGesture(gesture);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        tv_gesture = (TextView) findViewById(R.id.gestureOnVideo);

        int currentStepNum = 0;
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

        // test turn off screen
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
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked OK button
//            }
//        });

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
                if (!isImageOn) {
                    videoFragment.replay();
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

        // read the text file containing instruction for the current step
        try {
            String instruction = null;
            FileReader fileReader = new FileReader(Constants.sdCardDirectory + currentFileName + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            instruction = bufferedReader.readLine();
            Log.i(TAG, "Instruction = " + instruction);
            alert(instruction);

            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
