package com.example.drdc_admin.wearablephone.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.drdc_admin.wearablephone.R;
import com.example.drdc_admin.wearablephone.classes.Step;
import com.example.drdc_admin.wearablephone.listeners.StepListener;

import java.util.List;

/**
 * Created by DRDC_Admin on 11/01/2016.
 */
public class StepListAdapter extends BaseAdapter {

    private Context context;
    private List<Step> stepList;

    public StepListAdapter(Context ctx, List<Step> list) {
        stepList = list;
        context = ctx;
    }
    @Override
    public int getCount() {
        return stepList.size();
    }

    @Override
    public Object getItem(int i) {
        return stepList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return stepList.indexOf(getItem(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Step step = (Step) stepList.get(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.fragment_step_list_item, null);

        ImageView lessonImg = (ImageView) convertView.findViewById(R.id.lesson_img);
        TextView lessonTitle = (TextView) convertView.findViewById((R.id.lesson_title));
        TextView lessonDescription = (TextView) convertView.findViewById((R.id.lesson_description));
        ImageView playImg = (ImageView) convertView.findViewById(R.id.play_button);

        lessonTitle.setText(step.getTitle());
        lessonDescription.setText(step.getDescription());
        lessonImg.setImageResource(step.getImgRID());

        // set onClickListener for the LinearLayout containing title, image, description of the course
        StepListener stepListener = new StepListener(context, step.getVideoRID(), step.getVideoFileName());
        playImg.setOnClickListener(stepListener);

        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.lessonBox);


        return convertView;
    }
}
