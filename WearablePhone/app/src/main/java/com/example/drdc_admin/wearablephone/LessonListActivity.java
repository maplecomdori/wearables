package com.example.drdc_admin.wearablephone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.example.drdc_admin.wearablephone.adapters.LessonListAdapter;
import com.example.drdc_admin.wearablephone.classes.Lesson;

import java.util.ArrayList;
import java.util.List;

public class LessonListActivity extends AppCompatActivity {

    ListView listview;
    List<Lesson> lessons;
    List<String> listTitles;
    List<String> listDescriptions;
    int drawableID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        listview = (ListView) findViewById(R.id.lessonListView);

        // TODO:    get list of all the courses instead of hardcoding
        // prepareImgFile();

        hardcoding();

        LessonListAdapter adapter = new LessonListAdapter(this, lessons);

        listview.setAdapter(adapter);



    }
    /**
     * add course contents instead of retrieving it from database
     */
    private void hardcoding() {
        Lesson testone = new Lesson("Chopper", "Build a chopper");
        Lesson testtwo = new Lesson("Airplane", "Build an airplane");
        testone.setDrawableID(R.drawable.lesson_one);

        lessons = new ArrayList<Lesson>();
        lessons.add(testone);

    }


}
