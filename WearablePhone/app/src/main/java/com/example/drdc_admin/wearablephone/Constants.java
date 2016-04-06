package com.example.drdc_admin.wearablephone;

/**
 * Created by DRDC_Admin on 01/10/2015.
 */
public class Constants {

    // BLUETOOTH SERVICE
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final String BLUETOOTH_CONNECTION_RESULT = "BT_RESULT";
    public static final String VIDEO_RID = "videoRID";
    public static final String VIEDEO_FILENAME = "videoFilename";
    public static final String TITLE_STEP = "titleStep";
    public static final String TITLE_COURSE = "titleCourse";
    public static final String MYO_GESTURE = "gesture";

    public static final int NUM_AIRPLANE_TOTAL_STEPS = 34;
    public static final int NUM_AIRPLANE_PHASE1_STEPS = 19;
    public static final int NUM_AIRPLANE_PHASE2_STEPS = 15;

    public static final int NUM_HELICOPTER_TOTLAL_STEPS = 41;
    public static final int NUM_HELICOPTER_PHASE1_STEPS = 20;
    public static final int NUM_HELICOPTER_PHASE2_STEPS = 20;



}
