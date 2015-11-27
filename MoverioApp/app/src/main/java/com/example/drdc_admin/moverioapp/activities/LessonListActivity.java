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

import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.adapters.LessonListAdapter;
import com.example.drdc_admin.moverioapp.classes.Lesson;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying all the steps for a particular course from CourseListActivity
 * Make your selection using myo gestures in this activity
 */
public class LessonListActivity extends AppCompatActivity {

    private static final String TAG = "LessonListActivity";

    ListView listview;
    List<Lesson> lessons;
    List<String> listTitles;
    List<String> listDescriptions;
    int drawableID;
    public static int counter = 0;
    LessonListAdapter adapter;

    /**
     *     called when LocalBroadcastManager (from MainActivity) sends something
     *     enables getting and handling messages when this activity is the current activity
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String myoGesture = intent.getStringExtra("gesture");
            Log.i(TAG, "Got message: " + myoGesture);
            Log.i(TAG, "position = " + counter);

            handleGesture(context, myoGesture);

            // regenerate the listview
            listview.setAdapter(adapter);

        }
    };

    /**
     * translate the given myo gesture and reflect it in this activity
     * @param context
     * @param gesture myo gesture string sent from the phone
     */
    private void handleGesture(Context context, String gesture) {
        switch (gesture) {
            case "wave out": // move to the next item in the list
                counter++;
                if (lessons.size() <= counter) {
                    counter = 0;
                }
                break;
            case "wave in": // move to the prev item in the list
                counter--;
                if (counter < 0) {
                    counter = lessons.size() - 1;
                }
                break;
            case "fist": // select this item in the list to study this step
                Log.i(TAG, "item = " + adapter.getItem(counter));
                int videoRID = ((Lesson) adapter.getItem(counter)).getVideoRID();
                Intent i = new Intent(context, StudyLessonActivity.class);
                i.putExtra("RID", videoRID);
                startActivity(i);

                // reset position value for later
                counter = 0;
                break;
            case "fingers spread":
                finish(); // back button
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        listview = (ListView) findViewById(R.id.lessonListView);
        // TODO:    get list of all the courses instead of hardcoding
        // prepareImgFile();

        hardcoding();

        adapter = new LessonListAdapter(this, lessons);

        listview.setAdapter(adapter);

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
     * manually add steps instead of retrieving it from database
     */
    private void hardcoding() {
        Lesson lessonOne = new Lesson("Step1", "Add some part");
        Lesson lessonTwo = new Lesson("Step2", "remove some part");
        lessonOne.setImgRID(R.drawable.lesson_one);
        lessonOne.setVideoFileName("lesson2_step_2");
        lessonOne.setVideoRID(R.raw.lesson2_step_2);
        lessonTwo.setImgRID(R.drawable.lesson_two);
        lessonTwo.setVideoFileName("lesson2_step_3");
        lessonTwo.setVideoRID(R.raw.tutorial);

        lessons = new ArrayList<Lesson>();
        lessons.add(lessonOne);
        lessons.add(lessonTwo);


    }
}
