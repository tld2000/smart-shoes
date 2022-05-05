package com.project.smartshoes.datahandler;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ActivityRecordingSession {
    public static enum RecordSessionState {
        IDLE,
        STARTED,
        STOPPED
    }
    public static enum SensorSide {
        LEFT,
        RIGHT
    }
    private final String TAG = "ActivityRecordingSession";

    private long startTime = -1;
    private long endTime = -1;
    private AppCompatActivity activity;
    private String sessionID;
    private File dataFilePath;
    private String LDeviceAddress;
    private String RDeviceAddress;



    public ActivityRecordingSession(AppCompatActivity activity, String LDeviceAddress, String RDeviceAddress){
        this.activity = activity;
        this.LDeviceAddress = LDeviceAddress;
        this.RDeviceAddress = RDeviceAddress;
        //recorder = new ActivityRecorder(); //TODO: create ActivityRecorder class
    }


    public void createDataFile(){
        File parentDataPath = new File(activity.getApplicationContext().getFilesDir() + "/data");
        if (!parentDataPath.exists()){
            parentDataPath.mkdir();
        }

        try {
            dataFilePath = new File(activity.getApplicationContext().getFilesDir() + "/data", sessionID);
            dataFilePath.mkdir();
            File dataFileL = new File(dataFilePath, "L.txt");
            File dataFileR = new File(dataFilePath, "R.txt");
            dataFileL.createNewFile();
            dataFileR.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Error creating new data file directory " + dataFilePath.getPath());
            dataFilePath = null;
        }
    }


    public RecordSessionState getStatus(){
        if (startTime == -1)
            return RecordSessionState.IDLE;
        if (endTime == -1)
            return RecordSessionState.STARTED;
        return RecordSessionState.STOPPED;
    }


    public void startRecording(){
        startTime = System.currentTimeMillis();
        sessionID = String.valueOf(startTime);
        createDataFile();
        //recorder.start(sessionID);
    }


    public void stopRecording(){
        endTime = System.currentTimeMillis();
        //recorder.stop();
    }


    public void record(String writeValue, SensorSide side) throws IOException {
        da

        if (dataFile == null || !dataFile.exists()){
            Log.e(TAG, "Data file not created.");
            throw new IllegalArgumentException("Data file not created.");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, true));
            writer.append(writeValue + System.lineSeparator());
        } catch (IOException e){
            Log.e(TAG, "Write to file failed at " + dataFile.getPath());
            throw e;
        }

    }
}
