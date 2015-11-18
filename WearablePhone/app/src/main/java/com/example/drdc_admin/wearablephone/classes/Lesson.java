package com.example.drdc_admin.wearablephone.classes;

/**
 * Created by DRDC_Admin on 13/10/2015.
 */
public class Lesson {
    private int number; // EX) Lesson #1
    private String title;
    private String description;
    private int drawableID;     // imagefile
    private int videofileRID;   // video file for the lesson


    public Lesson(String title, String desc) {
        this.title = title;
        this.description = desc;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDrawableID() {
        return drawableID;
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }

    public int getVideofileRID() {
        return videofileRID;
    }

    public void setVideofileRID(int videofileRID) {
        this.videofileRID = videofileRID;
    }
}
