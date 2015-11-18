package com.example.drdc_admin.wearablephone.listners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.drdc_admin.wearablephone.WatchLessonActivity;

/**
 * Created by DRDC_Admin on 13/10/2015.
 */
public class LessonListener implements View.OnClickListener {

    private Context context;
    private String lessonTitle;

    public LessonListener(Context ctx, String title) {
        context = ctx;
        lessonTitle = title;
    }

    /**
     * Called when a view has been clicked
     * pass which course the user wants to LessonListActivity
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, WatchLessonActivity.class);
        intent.putExtra("lesson", lessonTitle);
        context.startActivity(intent);
    }
}
