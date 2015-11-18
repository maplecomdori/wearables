package com.example.drdc_admin.moverioapp.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.drdc_admin.moverioapp.activities.StudyLessonActivity;

/**
 * Created by DRDC_Admin on 13/10/2015.
 */
public class LessonListener implements View.OnClickListener {

    private Context context;
    private int videoRID;

    public LessonListener(Context ctx, int rid) {
        context = ctx;
        videoRID = rid;
    }

    /**
     * Called when a view has been clicked
     * pass which course the user wants to LessonListActivity
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, StudyLessonActivity.class);
        intent.putExtra("RID", videoRID);
        context.startActivity(intent);
    }
}
