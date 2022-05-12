package com.project.smartshoes.datahandler;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.project.smartshoes.MainActivity;
import com.project.smartshoes.SelectDeviceActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class Profile {
    private final String TAG = "Profile";
    private final String CONFIG_FILE_NAME = "config.properties";
    public static final double BARO_DIST = 0.2;   // distance between 2 barometers, in meters
    private double LBaroDiff = 0;    // value difference between the front and the back barometer of the left device
    private double RBaroDiff = 0;    // value difference between the front and the back barometer of the right device
    private final AppCompatActivity activity;
    private String LSensorAddress;
    private String RSensorAddress;


    public Profile(AppCompatActivity activity){
        this.activity = activity;
    }

    public boolean activate(){
        return loadConfig();
    }

    public void calibrate() throws IOException {
        // get BT address value
        getBTAddress();

        // TODO: add get baroValues
        double frontLBaroValue = 0;
        double backLBaroValue = 0;

        LBaroDiff = frontLBaroValue - backLBaroValue;

        File configFile = new File(activity.getApplicationContext().getFilesDir(), CONFIG_FILE_NAME);
        Properties properties = new Properties();
        try {
            // try loading old config
            properties.load(new FileInputStream(configFile));
        } catch (IOException e) {
            Log.e(TAG, "Creating new config file " + configFile.getPath());
            try {
                // no config created
                configFile.createNewFile();
            } catch (IOException ioException) {
                Log.e(TAG, "Error creating new config file " + configFile.getPath());
                throw new IOException("Error creating new config file.");
            }
        }

        properties.setProperty("LeftBaroDiff", String.valueOf(LBaroDiff));
        properties.setProperty("RightBaroDiff", String.valueOf(RBaroDiff));
        try {
            // create new config
            properties.store(new FileOutputStream(configFile), null);
        } catch (IOException e) {
            Log.e(TAG, "Error saving new config file " + configFile.getPath());
            throw new IOException("Error saving config file.");
        }
    }


    public boolean loadConfig() {
        File configPath = activity.getApplicationContext().getFilesDir();
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(new File(configPath, CONFIG_FILE_NAME)));
            LBaroDiff = Double.parseDouble(properties.getProperty("LeftBaroDiff"));
            RBaroDiff = Double.parseDouble(properties.getProperty("RightBaroDiff"));
            LSensorAddress = properties.getProperty("LSensorAddress");
            RSensorAddress = properties.getProperty("RSensorAddress");

            if (LSensorAddress == null || RSensorAddress == null)
                return false;
            return true;
        } catch (IOException | NullPointerException e) {
            Log.e(TAG, "Unable to load the config file: " + e.getMessage());
            return false;
        }
    }


    public double getBaroDiff(ActivityRecordingSession.SensorSide side){
        if (side == ActivityRecordingSession.SensorSide.LEFT)
            return LBaroDiff;
        return RBaroDiff;
    }


    public String getSensorAddress(ActivityRecordingSession.SensorSide side){
        if (side == ActivityRecordingSession.SensorSide.LEFT)
            return LSensorAddress;
        return RSensorAddress;
    }


    private void getBTAddress() {
        Intent intent = new Intent(activity, SelectDeviceActivity.class);
        activity.startActivity(intent);
    }
}
