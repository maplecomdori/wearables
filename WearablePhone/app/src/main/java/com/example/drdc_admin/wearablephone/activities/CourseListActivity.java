package com.example.drdc_admin.wearablephone.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.example.drdc_admin.wearablephone.R;
import com.example.drdc_admin.wearablephone.adapters.CourseListAdapter;
import com.example.drdc_admin.wearablephone.classes.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {

    private static final String TAG = "CourseListActivity";
    public static String course = "";

    ListView listview;
    List<Course> courses;
    CourseListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview = (ListView) findViewById(R.id.courseListView);

        // TODO:    get list of all the courses from moodle instead of hardcoding

        hardcoding();

        adapter = new CourseListAdapter(this, courses);
        listview.setAdapter(adapter);


        // OVERALL EXPERIMENT INTRO
        showIntro();
    }

    private void showIntro() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(Constants.MECANNO_INTRO);
        builder.setMessage(getString(R.string.course_intro));

        builder.setTitle("Introduction");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * manually add course contents instead of retrieving it from database
     */
    private void hardcoding() {

        Course airplanePhase1 = new Course(getString(R.string.airplanePhase1Title), getString(R.string.airplanePhase1Description));
        Course airplanePhase2 = new Course(getString(R.string.airplanePhase2Title), getString(R.string.airplanePhase2Description));

//        Course helicopterPhase1 = new Course(Constants.HELICOPTER_PHASE1_TITLE, "Build a chopper");
//        Course helicopterPhase2 = new Course(Constants.HELICOPTER_PHASE2_TITLE, "Build a chopper");


        airplanePhase1.setDrawableID(R.drawable.course_one);
        airplanePhase2.setDrawableID(R.drawable.course_one);
        // can't figure out how many files are in the SD card in the Moverio in this app without bluetooth connection
        airplanePhase1.setNumSteps(19);
        airplanePhase1.setEndOfPhase(19);
        airplanePhase2.setNumSteps(15);
        airplanePhase2.setEndOfPhase(34);

//        helicopterPhase1.setDrawableID(R.drawable.course_two);
//        helicopterPhase2.setDrawableID(R.drawable.course_two);

        courses = new ArrayList<Course>();
        courses.add(airplanePhase1);
        courses.add(airplanePhase2);
//        courses.add(helicopterPhase1);
//        courses.add(helicopterPhase2);

    }
}
