package com.example.drdc_admin.wearablephone.activities;

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

    }

    /**
     * manually add course contents instead of retrieving it from database
     */
    private void hardcoding() {
        Course airplanePhase1 = new Course("Airplane Phase #1", "Build an airplane");
        Course helicopterPhase1 = new Course("Chopper Phase #1", "Build a chopper");
        Course airplanePhase2 = new Course("Airplane Phase #2", "Build an airplane");
        Course helicopterPhase2 = new Course("Chopper Phase #2", "Build a chopper");

        airplanePhase1.setDrawableID(R.drawable.course_one);
        airplanePhase2.setDrawableID(R.drawable.course_one);
        helicopterPhase1.setDrawableID(R.drawable.course_two);
        helicopterPhase2.setDrawableID(R.drawable.course_two);

        courses = new ArrayList<Course>();
        courses.add(airplanePhase1);
        courses.add(airplanePhase2);
        courses.add(helicopterPhase1);
        courses.add(helicopterPhase2);

    }
}
