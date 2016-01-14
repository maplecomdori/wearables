package com.example.drdc_admin.moverioapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.activities.StepListActivity;
import com.example.drdc_admin.moverioapp.classes.Step;
import com.example.drdc_admin.moverioapp.listeners.StepListener;

import java.util.List;

/**
 * Created by DRDC_Admin on 13/10/2015.
 */
public class StepListAdapter extends BaseAdapter {

    private Context context;
    private List<Step> stepList;
    private int[] gridImageId;

    public StepListAdapter(Context ctx, List<Step> list) {
        stepList = list;
        context = ctx;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return stepList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return stepList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return stepList.indexOf(getItem(position));
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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


        // if this is the selected one, change the color
        if (position == StepListActivity.listPosition) {
            relativeLayout.setBackgroundResource(R.drawable.list_item_selected);
        }

        return convertView;

    }
}
