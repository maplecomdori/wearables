package com.example.drdc_admin.moverioapp.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.drdc_admin.moverioapp.Constants;
import com.example.drdc_admin.moverioapp.activities.ContentActivity;

/**
 * Created by DRDC_Admin on 13/10/2015.
 */
public class StepListener implements View.OnClickListener {

    private Context context;
    private int videoRID;
    private String filename;

    public StepListener(Context ctx, int rid, String file) {
        context = ctx;
        videoRID = rid;
        filename = file;
    }

    /**
     * Called when a view has been clicked
     * pass which course the user wants to StepListActivity
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(Constants.VIDEO_RID, videoRID);
        intent.putExtra(Constants.VIEDEO_FILENAME, filename);
        context.startActivity(intent);
    }
}
