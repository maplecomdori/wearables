package com.example.drdc_admin.wearablephone.listners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.drdc_admin.wearablephone.LessonListActivity;

/**
 * Created by DRDC_Admin on 13/10/2015.
 */
public class CourseListener implements View.OnClickListener {

    private Context context;
    private String courseTitle;

    public CourseListener(Context ctx, String title) {
        context = ctx;
        courseTitle = title;
    }

    /**
     * Called when a view has been clicked
     * pass which course the user wants to LessonListActivity
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, LessonListActivity.class);
        intent.putExtra("title", courseTitle);
        context.startActivity(intent);
    }
}
