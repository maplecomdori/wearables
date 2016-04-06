package com.example.drdc_admin.wearablephone.classes;

/**
 * Created by DRDC_Admin on 11/01/2016.
 */
public class Step {

    private int number; // EX) Step #1
    private String title;
    private String description;
    private int imgRID;     // imagefile R.id.####
    private String fileName;// without extension
    private String instruction;
    private String phase; // phase that contains this step

    public Step() {

    }

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

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}

