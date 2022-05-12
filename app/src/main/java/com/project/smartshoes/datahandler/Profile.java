package com.project.smartshoes.datahandler;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.project.smartshoes.MainActivity;
import com.project.smartshoes.SelectDeviceActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class Profile {
    private final String TAG = "Profile";
    private final String CONFIG_FILE_NAME = "config.properties";
    public static final double BARO_DIST = 0.2;   // distance between 2 barometers, in meters
    private double baroDiff = 0;    // value difference between the front and the back barometer of the left device
    private final AppCompatActivity activity;


    public Profile(AppCompatActivity activity){
        this.activity = activity;
    }

    public void calibrate() {

        baroDiff = getBaroDiff();

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
            }
        }

        properties.setProperty("baroDiff", String.valueOf(baroDiff));
        try {
            // create new config
            properties.store(new FileOutputStream(configFile), null);
        } catch (IOException e) {
            Log.e(TAG, "Error saving new config file " + configFile.getPath());
        }
    }


    public boolean loadConfig() {
        File configPath = activity.getApplicationContext().getFilesDir();
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(new File(configPath, CONFIG_FILE_NAME)));
            String baroDiffString = properties.getProperty("baroDiff");
            if (baroDiffString == null )
                return false;

            baroDiff = Double.valueOf(baroDiffString);
            return true;
        } catch (IOException | NullPointerException e) {
            Log.e(TAG, "Unable to load the config file: " + e.getMessage());
            return false;
        }
    }


    public double getBaroDiff(){
        double avrBaro1 = 0;
        double avrBaro2 = 0;
        int count = 0;

        // read that csv file
        File parentDataPath = new File(String.valueOf(activity.getApplicationContext().getFilesDir()));
        try (BufferedReader br = new BufferedReader(new FileReader(parentDataPath + "/calibrationData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                avrBaro1 += Double.valueOf(values[0]);
                avrBaro2 += Double.valueOf(values[1]);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (avrBaro1 - avrBaro2)/count;
    }


    public void createCalibrationFile(){
        File parentDataPath = new File(String.valueOf(activity.getApplicationContext().getFilesDir()));
        try {
            File calibrationFile = new File(parentDataPath, "calibrationData.txt");
            calibrationFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Error creating calibration data file directory " + parentDataPath.getAbsoluteFile().getPath() + "/calibrationData.txt");
        }
    }


    public void calibrationDataWrite(String writeValue) {
        File parentDataPath = new File(String.valueOf(activity.getApplicationContext().getFilesDir()));
        File calibrationFile = new File(parentDataPath, "calibrationData.txt");

        if (!calibrationFile.exists()){
            Log.e(TAG, "Data file not created.");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(calibrationFile, true));
            writer.append(writeValue + System.lineSeparator());
        } catch (IOException e){
            Log.e(TAG, "Write to file failed at " + calibrationFile.getPath());
        }
    }
}
