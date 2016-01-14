package com.example.drdc_admin.wearablephone.classes;

/**
 * Created by DRDC_Admin on 11/01/2016.
 */
public class Step {

    private int number; // EX) Step #1
    private String title;
    private String description;
    private int imgRID;     // imagefile R.id.####
    private String videoFileName;
    private String instruction;

    private int videoRID;  // video file for the lesson R.id.####


    public Step(String title, String desc) {
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

    public int getImgRID() {
        return imgRID;
    }

    public void setImgRID(int imgRID) {
        this.imgRID = imgRID;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public int getVideoRID() {
        return videoRID;
    }

    public void setVideoRID(int videoRID) {
        this.videoRID = videoRID;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}

