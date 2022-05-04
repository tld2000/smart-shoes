package com.project.smartshoes.datahandler;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Profile {
    private final String TAG = "Profile";
    private final String CONFIGFILENAME = "config.properties";
    private final double BARODIST = 0.2;   // distance between 2 barometers, in meters
    private double baroDiff = 0;    // value difference between the front and the back barometer
    private final AppCompatActivity activity;


    public Profile(AppCompatActivity activity){
        this.activity = activity;
    }

    public boolean activate(){
        return loadConfig();
    }

    public void calibrate() throws IOException {
        // TODO: add get baroValues
        double frontBaroValue = 0;
        double backBaroValue = 0;

        baroDiff = frontBaroValue - backBaroValue;

        File configFile = new File(activity.getApplicationContext().getFilesDir(), CONFIGFILENAME);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configFile));
        } catch (IOException e) {
            Log.e(TAG, "Creating new config file " + configFile.getPath());
            try {
                configFile.createNewFile();
            } catch (IOException ioException) {
                Log.e(TAG, "Error creating new config file " + configFile.getPath());
                throw new IOException("Error creating new config file.");
            }
        }

        properties.setProperty("baroDiff", String.valueOf(baroDiff));
        try {
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
            properties.load(new FileInputStream(new File(configPath, CONFIGFILENAME)));
            baroDiff = Double.parseDouble(properties.getProperty("baroDiff"));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Unable to load the config file: " + e.getMessage());
            return false;
        }
    }

    public double getBaroDiff() {
        return baroDiff;
    }
}
