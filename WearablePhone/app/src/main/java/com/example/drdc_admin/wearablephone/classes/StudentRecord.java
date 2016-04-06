package com.example.drdc_admin.wearablephone.classes;

import com.example.drdc_admin.wearablephone.Constants;

/**
 * Created by DRDC_Admin on 04/04/2016.
 */
public class StudentRecord {

    public static String RECORD_AIRPLANE = "airplane";
    public static String RECORD_HELICOPTER = "helicopter";

    private int airplaneStepRecord[];
    private int helicopterStepRecord[];

    public StudentRecord() {
        for(int i = 0; i < Constants.NUM_AIRPLANE_TOTAL_STEPS; i++) {
            airplaneStepRecord[i] = 0;
        }
        for(int i = 0; i < Constants.NUM_HELICOPTER_TOTLAL_STEPS; i++) {
            helicopterStepRecord[i] = 0;
        }

    }

    public void setRecord(String type,int stepNumber, int time) {
        int index = stepNumber - 1;
        if(type.equals(RECORD_AIRPLANE)) {
            airplaneStepRecord[index] = time;
        }
        else if(type.equals(RECORD_HELICOPTER)) {
            helicopterStepRecord[index] = time;
        }
    }

    public int getRecord(String type, int stepNumber, int time) {
        int index = stepNumber - 1;

        if(type.equals(RECORD_AIRPLANE)) {
            return airplaneStepRecord[index];
        }
        else if(type.equals(RECORD_HELICOPTER)) {
            return helicopterStepRecord[index];
        }

        return -1;
    }

}
