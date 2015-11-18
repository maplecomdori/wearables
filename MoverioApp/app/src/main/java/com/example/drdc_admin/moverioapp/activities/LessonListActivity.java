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

public class LessonListActivity extends AppCompatActivity {

    private static final String TAG = "LessonListActivity";

    ListView listview;
    List<Lesson> lessons;
    List<String> listTitles;
    List<String> listDescriptions;
    int drawableID;
    public static int counter = 0;
    LessonListAdapter adapter;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String myoGesture = intent.getStringExtra("gesture");
            Log.i(TAG, "Got message: " + myoGesture);
            Log.i(TAG, "counter = " + counter);

            handleGesture(context, myoGesture);

            // refill the listview
            listview.setAdapter(adapter);

        }
    };

    private void handleGesture(Context context, String gesture) {
        switch (gesture) {
            case "wave out":
                counter++;
                if (lessons.size() <= counter) {
                    counter = 0;
                }
                break;
            case "wave in":
                counter--;
                if (counter < 0) {
                    counter = lessons.size() - 1;
                }
                break;
            case "fist":
                // start lesson
                Log.i(TAG, "item = " + adapter.getItem(counter));
                int videoRID = ((Lesson) adapter.getItem(counter)).getVideoRID();
                Intent i = new Intent(context, StudyLessonActivity.class);
                i.putExtra("RID", videoRID);
                startActivity(i);

                // reset counter value for later
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("myo-event"));
    }


    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }




    /**
     * add course contents instead of retrieving it from database
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
