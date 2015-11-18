package com.example.drdc_admin.moverioapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.adapters.CourseListAdapter;
import com.example.drdc_admin.moverioapp.classes.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {

    private static final String TAG = "CourseListActivity";

    ListView listview;
    List<Course> courses;
    List<String> listTitles;
    List<String> listDescriptions;
    int drawableID;
    CourseListAdapter adapter;
    public static int counter = 0;

    // called when LocalBroadcastManager sends something
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String myoGesture = intent.getStringExtra("gesture");
            Log.i(TAG, "Got message: " + myoGesture);
            Log.i(TAG, "counter = " + counter);
//            Log.i(TAG, "is courses null? " + courses.toString());

            // take actions depending on the passed gesture
            handleGesture(context, myoGesture);

            // refill the listview
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
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("myo-event"));
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    /**
     * add course contents instead of retrieving it from database
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

    private void handleGesture(Context context, String gesture) {
        int h1 = listview.getHeight();
        int h2 = adapter.getView(counter, listview, listview).getHeight();
        Log.i(TAG, "" + h1 + " " + h2);
        listview.setSelectionFromTop(counter, h1/2 - h2/2);
        listview.setSelection(1);

        switch (gesture) {
            case "wave out":

                counter++;
                if (courses.size() <= counter) {
                    counter = 0;
                }
                break;
            case "wave in":
//                listview.setSelection(counter);
                counter--;
                if (counter < 0) {
                    counter = courses.size() - 1;
                }
                break;
            case "fist":
                // start lesson
                Log.i(TAG, "item = " + adapter.getItem(counter));
                String courseTitle = ((Course) adapter.getItem(counter)).getTitle();
                Intent i = new Intent(context, LessonListActivity.class);
                i.putExtra("title", courseTitle);
                startActivity(i);

                // reset counter value for later
                counter = 0;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Launch the DeviceListActivity to see devices and do scan
            case R.id.searchBluetooth: {
                Intent searchIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(searchIntent, 0); // 0 = REQUEST_CONNECT_DEVICE
                return true;
            }
        }
        return false;
    }

}
