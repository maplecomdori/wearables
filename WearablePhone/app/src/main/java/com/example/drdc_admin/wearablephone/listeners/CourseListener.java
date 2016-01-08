package com.example.drdc_admin.wearablephone.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.drdc_admin.wearablephone.Constants;
import com.example.drdc_admin.wearablephone.activities.StepListActivity;

/**
 * Created by DRDC_Admin on 08/01/2016.
 */
public class CourseListener  implements View.OnClickListener {

    private Context context;
    private String courseTitle;

    public CourseListener(Context ctx, String title) {
        context = ctx;
        courseTitle = title;
    }

    /**
     * Called when a view has been clicked
     * pass which course the user wants to StepListActivity
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, StepListActivity.class);
        intent.putExtra(Constants.TITLE_COURSE, courseTitle);
        context.startActivity(intent);
    }
}

