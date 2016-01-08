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
import com.example.drdc_admin.wearablephone.classes.Course;
import com.example.drdc_admin.wearablephone.listeners.CourseListener;

import java.util.List;

/**
 * Created by DRDC_Admin on 08/01/2016.
 */
public class CourseListAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courseList;
    private int[] gridImageId;
    private static final String TAG = "CourseListAdapter";

    /**
     * constructor
     * @param ctx
     * @param list list of courses
     */
    public CourseListAdapter(Context ctx, List<Course> list) {
        courseList = list;
        context = ctx;
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return courseList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Course course = (Course) courseList.get(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.fragment_course_list_item, null);

        ImageView courseImg = (ImageView) convertView.findViewById(R.id.course_img);
        TextView courseTitle = (TextView) convertView.findViewById((R.id.course_title));
        TextView courseDescription = (TextView) convertView.findViewById((R.id.course_description));

        courseTitle.setText(course.getTitle());
        courseDescription.setText(course.getDescription());
        courseImg.setImageResource(course.getDrawableID());

        // set onClickListener for the LinearLayout containing title, image, description of the course
        CourseListener courseListener = new CourseListener(context, course.getTitle());

        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.courseBox);
        relativeLayout.setOnClickListener(courseListener);



        return convertView;
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView _imageView;
        TextView courseTitle;
        TextView courseDescription;

    }

}
