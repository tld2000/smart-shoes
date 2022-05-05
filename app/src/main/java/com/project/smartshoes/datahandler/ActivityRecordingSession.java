package com.project.smartshoes.datahandler;

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
    public static enum SensorSide {
        LEFT,
        RIGHT
    }
    private final String TAG = "ActivityRecordingSession";
    private final String START_CMD = "<start>";
    private final String SOP_CMD = "<stop>";

    private long startTime = -1;
    private long endTime = -1;
    private AppCompatActivity activity;
    private Profile profile;
    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    private String sessionID;
    private File dataFilePath;
    private ArrayList<String> sensorAddresses = new ArrayList<String>();



    public ActivityRecordingSession(AppCompatActivity activity, Handler handler, BluetoothAdapter bluetoothAdapter){
        this.activity = activity;
        this.handler = handler;
        this.bluetoothAdapter = bluetoothAdapter;
        profile = new Profile(activity);
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
        loadProfile();
        createDataFile();
        sendStartSignal();
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


    public void loadProfile() {
        if (profile.loadConfig()){
            Log.e(TAG, "Failed to load profile config.");
            throw new IllegalArgumentException("Failed to load Profile, please recalibrate");
        }
        sensorAddresses.add(profile.getSensorAddress(SensorSide.LEFT));
        sensorAddresses.add(profile.getSensorAddress(SensorSide.RIGHT));
    }


    public void sendStartSignal(){
        for (String BTAddress : sensorAddresses) {
            MainActivity.CreateConnectThread createConnectThread = new MainActivity.CreateConnectThread(bluetoothAdapter, deviceAddress);
            createConnectThread.start();
        }
    }


}
