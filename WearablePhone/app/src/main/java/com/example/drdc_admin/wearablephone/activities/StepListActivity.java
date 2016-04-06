package com.example.drdc_admin.wearablephone.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.example.drdc_admin.wearablephone.Constants;
import com.example.drdc_admin.wearablephone.R;
import com.example.drdc_admin.wearablephone.adapters.StepListAdapter;
import com.example.drdc_admin.wearablephone.classes.Step;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StepListActivity extends AppCompatActivity {

    private static final String TAG = "StepListActivity";
    ListView listview;
    List<Step> steps;
    String courseTitle;
    String pathToStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview = (ListView) findViewById(R.id.stepListView);
        // TODO:    get list of all the courses instead of hardcoding

        Intent intent = getIntent();
        courseTitle = intent.getStringExtra(Constants.TITLE_COURSE);

        getSupportActionBar().setTitle(courseTitle);

        pathToStorage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/airplane/";

        hardcoding();

        StepListAdapter adapter = new StepListAdapter(this, steps);

        listview.setAdapter(adapter);

        // PHASE INTRO
        showPhaseIntro();

    }

    private void showPhaseIntro() {

        String msg = null;
        if (courseTitle.equals(getString(R.string.airplanePhase1Title))) {
            msg = getString(R.string.airplanePhase1_popup);
        } else if (courseTitle.equals(getString(R.string.airplanePhase2Title))) {
            msg = getString(R.string.airplanePhase2_popup);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle("Welcome to " + courseTitle);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * manually add steps instead of retrieving it from database
     */
    private void hardcoding() {
        steps = new ArrayList<Step>();

        if (courseTitle.equals(getString(R.string.airplanePhase1Title))) {
            String stepNumber;
            for (int i = 1; i <= 19; i++) {

                if (i < 10) {
                    stepNumber = "0" + i;
                } else {
                    stepNumber = "" + i;
                }

                Step step = new Step();
                step.setTitle("Step #" + i);
                String filename = "airplanestep" + stepNumber;
                step.setFileName(filename);


                setDescription(step, filename);

                steps.add(step);

            }

        } else if (courseTitle.equals(getString(R.string.airplanePhase2Title))) {
            String stepNumber;
            for (int i = 20; i <= 34; i++) {

                stepNumber = "" + i;
                Step step = new Step();
                step.setTitle("Step #" + i);
                step.setDescription("DESCRIPTION");
                step.setInstruction("INSTRUCTION");
                step.setFileName("airplanestep" + stepNumber);
                steps.add(step);

            }

        }


//        Step stepOne = new Step("Step1", "Add some part");
//        Step stepTwo = new Step("Step2", "remove some part");
//        stepOne.setImgRID(R.drawable.lesson_one);
//        stepOne.setFileName("airplanestep1");
//        stepTwo.setImgRID(R.drawable.lesson_two);
//        stepTwo.setFileName("airplanestep10");
//
//        steps = new ArrayList<Step>();
//        steps.add(stepOne);
//        steps.add(stepTwo);


    }

    public void setDescription(Step step, String FileNameWithoutExt) {

        // read the text file containing instruction for the current step
        try {
            String description = null;

            FileReader fileReader = new FileReader(pathToStorage + FileNameWithoutExt + ".txt");

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            description = bufferedReader.readLine();
            bufferedReader.close();

            // remove "Step #N" from 'description'
            String array[] = description.split(":");
//            Log.i(TAG, "description = " + array[1].trim());
            step.setDescription(array[1].trim());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}