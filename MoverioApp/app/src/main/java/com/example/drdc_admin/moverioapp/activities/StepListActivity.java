package com.example.drdc_admin.moverioapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.adapters.LessonListAdapter;
import com.example.drdc_admin.moverioapp.classes.Lesson;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying all the steps for a particular course from CourseListActivity
 * Make your selection using myo gestures in this activity
 */
public class StepListActivity extends AppCompatActivity {

    private static final String TAG = "StepListActivity";

    ListView listview;
    List<Lesson> lessons;
    List<String> listTitles;
    List<String> listDescriptions;
    int drawableID;
    public static int counter = 0;
    LessonListAdapter adapter;
    private int menuItemPosition = 0;
    Toolbar toolbar;

    /**
     *     called when LocalBroadcastManager (from MainActivity) sends something
     *     enables getting and handling messages when this activity is the current activity
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String myoGesture = intent.getStringExtra("gesture");
            Log.i(TAG, "Got message: " + myoGesture);
            Log.i(TAG, "position = " + counter);

            handleGesture(context, myoGesture);

            // regenerate the listview
            listview.setAdapter(adapter);

        }
    };

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p/>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p/>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p/>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p/>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_step_list, menu);

        // check the first menu item as default
        toolbar.getMenu().getItem(menuItemPosition).setCheckable(true);
        toolbar.getMenu().getItem(menuItemPosition).setChecked(true);

        return true;
    }

    /**
     * translate the given myo gesture and reflect it in this activity
     * @param context
     * @param gesture myo gesture string sent from the phone
     */
    private void handleGesture(Context context, String gesture) {
        switch (gesture) {
            case "wave out": // move to the next item in the list
                counter++;
                if (lessons.size() <= counter) {
                    counter = 0;
                }
                break;
            case "wave in": // move to the prev item in the list
                counter--;
                if (counter < 0) {
                    counter = lessons.size() - 1;
                }
                break;
            case "fist": // select this item in the list to study this step
                Log.i(TAG, "item = " + adapter.getItem(counter));
                int videoRID = ((Lesson) adapter.getItem(counter)).getVideoRID();
                Intent i = new Intent(context, ContentActivity.class);
                i.putExtra("RID", videoRID);
                startActivity(i);

                // reset position value for later
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

        toolbar = (Toolbar) findViewById(R.id.stepListToolbar);
        toolbar.setTitle("Course Name");
        setSupportActionBar(toolbar);

        listview = (ListView) findViewById(R.id.lessonListView);
        // TODO:    get list of all the courses instead of hardcoding
        // prepareImgFile();

        hardcoding();

        adapter = new LessonListAdapter(this, lessons);

        listview.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goBack:
                finish();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages only when this activity is the current activity.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("myo-event"));
    }



    @Override
    protected void onPause() {
        super.onPause();
        // Deregister from BroadCastManager to prevent getting messages at unwanted times
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }


    /**
     * manually add steps instead of retrieving it from database
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
