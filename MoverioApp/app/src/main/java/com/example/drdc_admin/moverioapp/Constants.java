package com.example.drdc_admin.moverioapp;

/**
 * Created by DRDC_Admin on 01/10/2015.
 */
public class Constants {

    public static final int MESSAGE_TOAST = 5;
    public static final String BLUETOOTH_CONNECTION_RESULT = "BT_RESULT";
    public static final String VIDEO_RID = "videoRID";
    public static final String VIDEO_FILENAME = "videoFileName";
    public static final String TITLE_STEP = "titleStep";
    public static final String TITLE_COURSE = "titleCourse";
    public static final String VIDEO_DIRECTORY = "videoDirectory";
    public static final String MYO_GESTURE = "gesture";
    public static final String JSON_STRING = "jsonString";
    public static final String UPMENU = "up";
    public static final String DOWNMENU = "down";

    public static final String MSG_INTRO = "Please connect to the phone over Bluetooth " +
            "and make your selection on the phone to see the contents here. " +
            "Use the trackpad to click the OK button";

    // MYO GESTURES
    public static final String MYO_REST = "rest";
    public static final String MYO_FIST = "fist";
    public static final String MYO_WAVEOUT = "wave out";
    public static final String MYO_WAVEIN = "wave in";
    public static final String MYO_FINGERSPEREAD = "fingers spread";

    // DIFFERENT SDCARD PATH FOR DIFFERENT DEVICES
    public static final String DIRECTORY_AIRPLANE_PHASE1 = "";
    public static final String DIRECTORY_AIRPLANE_PHASE2 = "";
    public static final String DIRECTORY_HELICOPTOR_PHASE1 = "";
    public static final String DIRECTORY_HELICOPTOR_PHASE2 = "";



//    public static final String sdCardDirectory = "/storage/sdcard1/airplane/"; //S4 rooted
    public static final String sdCardDirectory = "/storage/extSdCard/airplane/"; //S4 unrooted
    public static final String pathToAirplane = "/root/sdcard/airplane/";

    // the main storage for moverio is /mnt/sdcard
//    public static final String sdCardDirectory = "/mnt/sdcard/airplane/"; //MOVERIO


}
