package com.example.drdc_admin.wearablephone.listeners;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.drdc_admin.wearablephone.activities.MainActivity;
import com.example.drdc_admin.wearablephone.classes.Course;
import com.example.drdc_admin.wearablephone.classes.Step;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by DRDC_Admin on 12/01/2016.
 */
public class StepListener implements View.OnClickListener {

    private static final String TAG = "StepListener";
    private Context context;
    private int videoRID;
    private String filename;
    private Course testCourse;
    private OutputStream outputStream;


    public StepListener(Context ctx, int rid, String file) {
        context = ctx;
        videoRID = rid;
        filename = file;
    }

    /**
     * Called when a view has been clicked
     * pass which course the user wants to StepListActivity
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(context, ContentActivity.class);
//        intent.putExtra(Constants.VIDEO_RID, videoRID);
//        intent.putExtra(Constants.VIEDEO_FILENAME, filename);
//        context.startActivity(intent);

        // start the content on Moverio
//        sendMsgToMoverio("test".getBytes());
        outputStream = MainActivity.outputStream;


        //TODO change to what the user has selected
        testCourse = new Course();
        testCourse.setDescription("testDescription");

        Step step = new Step("TITLE", "DESCRIPTION");
        step.setVideoFileName("airplanestep01");
        step.setInstruction("INSTRUCTION");

        Gson gson = new Gson();
        String jsonString = gson.toJson(step);
        Log.i(TAG, "jsonString = " + jsonString);
        sendMsgToMoverio(jsonString.getBytes());
    }


    private void sendMsgToMoverio(byte[] buffer) {
//        Log.e(TAG, "sendMsgToMoverio");
        if (outputStream != null) {
            try {
                outputStream.write(buffer);
                outputStream.flush();
//                 outputStream.close();
//                btMsg = "";

            } catch (IOException e) {
                Log.e(TAG, "write(buffer) failed " + e);
                e.printStackTrace();
            }
        } else {
//            Log.e(TAG, "outputStream is NULL");
        }
    }
}
