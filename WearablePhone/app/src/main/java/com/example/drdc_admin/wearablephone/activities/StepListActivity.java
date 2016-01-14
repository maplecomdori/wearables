package com.example.drdc_admin.wearablephone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.example.drdc_admin.wearablephone.R;
import com.example.drdc_admin.wearablephone.adapters.StepListAdapter;
import com.example.drdc_admin.wearablephone.classes.Step;

import java.util.ArrayList;
import java.util.List;

public class StepListActivity extends AppCompatActivity {

    private static final String TAG = "StepListActivity";
    ListView listview;
    List<Step> steps;
    StepListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview = (ListView) findViewById(R.id.stepListView);
        // TODO:    get list of all the courses instead of hardcoding
        // prepareImgFile();

        hardcoding();

        adapter = new StepListAdapter(this, steps);

        listview.setAdapter(adapter);
    }



    /**
     * manually add steps instead of retrieving it from database
     */
    private void hardcoding() {
        Step stepOne = new Step("Step1", "Add some part");
        Step stepTwo = new Step("Step2", "remove some part");
        stepOne.setImgRID(R.drawable.lesson_one);
        stepOne.setVideoFileName("airplanestep1");
        stepOne.setVideoRID(R.raw.airplane_step1);
        stepTwo.setImgRID(R.drawable.lesson_two);
        stepTwo.setVideoFileName("airplanestep10");
//        stepOne.setVideoRID(R.raw.airplane_step10);


        steps = new ArrayList<Step>();
        steps.add(stepOne);
        steps.add(stepTwo);


    }
}