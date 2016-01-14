package com.example.drdc_admin.moverioapp.classes;

import java.io.Serializable;

/**
 * A class representing a course
 */
public class Course implements Serializable {

    /**
     * FIELD
     * title and description of the course
     * drawableID refers to the R.drawable.[imagename]
     */
    private String title;
    private String description;
    private String imageFileName;
    private int drawableID;

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

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }


}

