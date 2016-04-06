package com.example.drdc_admin.wearablephone.classes;

import java.io.Serializable;

/**
 * A class representing a course (phase)
 */
public class Course implements Serializable {

    /**
     * FIELDs
     * title and description of the course
     * drawableID refers to the R.drawable.[imagename]
     */
    private String title;
    private String description;
    private int endOfPhase; // 19 for airplane
    private int drawableID;
    private int numSteps;



    public int getEndOfPhase() {
        return endOfPhase;
    }

    public void setEndOfPhase(int endOfPhase) {
        this.endOfPhase = endOfPhase;
    }



    public int getNumSteps() {
        return numSteps;
    }

    public void setNumSteps(int numSteps) {
        this.numSteps = numSteps;
    }


    public int getDrawableID() {
        return drawableID;
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }

    // default constructor
    public Course() {

    }

    public Course(String title, String description) {
        this.title = title;
        this.description = description;
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



}
