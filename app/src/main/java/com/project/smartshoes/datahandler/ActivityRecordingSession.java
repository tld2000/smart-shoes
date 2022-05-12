package com.project.smartshoes.datahandler;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.project.smartshoes.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ActivityRecordingSession {
    public static enum RecordSessionState {
        IDLE,
        STARTED,
        STOPPED
    }
    private final String TAG = "ActivityRecordingSession";
    private final String START_CMD = "<start>";
    private final String SOP_CMD = "<stop>";
    private double baroDiff;

    private long startTime = -1;
    private long endTime = -1;
    private AppCompatActivity activity;
    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    private String sessionID;
    private File dataFile;



    public ActivityRecordingSession(AppCompatActivity activity, Handler handler, BluetoothAdapter bluetoothAdapter){
        this.activity = activity;
        this.handler = handler;
        this.bluetoothAdapter = bluetoothAdapter;
        //recorder = new ActivityRecorder(); //TODO: create ActivityRecorder class
    }


    @SuppressLint("LongLogTag")
    public void createDataFile(){
        File parentDataPath = new File(activity.getApplicationContext().getFilesDir() + "/data");
        if (!parentDataPath.exists()){
            parentDataPath.mkdir();
        }

        try {
            File dataFile = new File(parentDataPath, "data.txt");
            dataFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, true));
            writer.append(baroDiff + System.lineSeparator());
            writer.append(Profile.BARO_DIST + System.lineSeparator());
        } catch (IOException e) {
            Log.e(TAG, "Error creating new data file directory " + dataFile.getPath());
            dataFile = null;
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
    }


    public void stopRecording(){
        endTime = System.currentTimeMillis();
        //recorder.stop();
    }


    public void record(String writeValue) {

        if (dataFile == null || !dataFile.exists()){
            Log.e(TAG, "Data file not created.");
            throw new IllegalArgumentException("Data file not created.");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, true));
            writer.append(writeValue + System.lineSeparator());
        } catch (IOException e){
            Log.e(TAG, "Write to file failed at " + dataFile.getPath());
        }
    }


    public void loadProfile(Profile profile) {
        if (!profile.loadConfig()){
            Log.e(TAG, "Failed to load profile config.");
            throw new IllegalArgumentException("Failed to load Profile, please recalibrate");
        }
        baroDiff = profile.getBaroDiff();
    }



}
