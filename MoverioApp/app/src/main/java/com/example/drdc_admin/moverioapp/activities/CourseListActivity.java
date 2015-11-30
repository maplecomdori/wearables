package com.example.drdc_admin.moverioapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.adapters.CourseListAdapter;
import com.example.drdc_admin.moverioapp.classes.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying all the courses for a particular student
 * Make your selection using myo gestures in this activity
 */

public class CourseListActivity extends AppCompatActivity {

    private static final String TAG = "CourseListActivity";
    public static String course = "";

    ListView listview;
    List<Course> courses;
    List<String> listTitles;
    List<String> listDescriptions;
    int drawableID;
    CourseListAdapter adapter;

    // position keeps track of the position in the list
    public static int position = 0;

    /**
     *     called when LocalBroadcastManager (from MainActivity) sends something
     *     enables getting and handling messages when this activity is the current activity
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String myoGesture = intent.getStringExtra("gesture");
            Log.i(TAG, "Got message: " + myoGesture);
            Log.i(TAG, "position = " + position);
//            Log.i(TAG, "is courses null? " + courses.toString());

            // take actions depending on the received gesture
            handleGesture(context, myoGesture);

            // regenerate the listview
            listview.setAdapter(adapter);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        listview = (ListView) findViewById(R.id.courseListView);
        TextView tv = (TextView) findViewById(R.id.courseListTitle);


        // TODO:    get list of all the courses from moodle instead of hardcoding
        // prepareImgFile();

        hardcoding();
//http://stackoverflow.com/questions/3263169/android-how-to-make-view-highlight-when-clicked
//http://stackoverflow.com/questions/16976431/change-background-color-of-selected-item-on-a-listview
        // http://stackoverflow.com/questions/26400354/highlight-textview-when-clicked-programmatically
        adapter = new CourseListAdapter(this, courses);
        listview.setAdapter(adapter);

    }

    private void prepareImgFile() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages only when this activity is the current activity.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("myo-event"));
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Deregister from BroadCastManager to prevent getting messages at unwanted times
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    /**
     * manually add course contents instead of retrieving it from database
     */
    private void hardcoding() {
        Course testone = new Course("Chopper", "Build a chopper");
        Course testtwo = new Course("Airplane", "Build an airplane");
        testone.setDrawableID(R.drawable.course_one);
        testtwo.setDrawableID(R.drawable.course_two);
        courses = new ArrayList<Course>();
        courses.add(testone);
        courses.add(testtwo);
//        courses.add(testtwo);



    }

    /**
     * translate the given myo gesture and reflect it in this activity
     * @param context
     * @param gesture myo gesture string sent from the phone
     */
    private void handleGesture(Context context, String gesture) {
        int h1 = listview.getHeight();
        int h2 = adapter.getView(position, listview, listview).getHeight();
        Log.i(TAG, "" + h1 + " " + h2);
        listview.setSelectionFromTop(position, h1/2 - h2/2);
        listview.setSelection(1);

        switch (gesture) {
            case "wave out": // move to the next item in the list
                position++;
                if (courses.size() <= position) {
                    position = 0;
                }
                break;
            case "wave in": // move to the prev item in the list
//                listview.setSelection(position);
                position--;
                if (position < 0) {
                    position = courses.size() - 1;
                }
                break;
            case "fist": // select this item in the list to go to LessonListActivity
                Log.i(TAG, "item = " + adapter.getItem(position));
                String courseTitle = ((Course) adapter.getItem(position)).getTitle();
                Intent i = new Intent(context, LessonListActivity.class);
                i.putExtra("title", courseTitle);
                startActivity(i);

                // reset position value for later
                position = 0;
                break;
        }
    }





}
