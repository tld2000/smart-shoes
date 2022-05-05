package com.project.smartshoes.datahandler;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Result {
    private final AppCompatActivity activity;
    private final String sessionID;
    private final File dataFile;

    public Result(AppCompatActivity activity, String sessionID) {
        this.activity = activity;
        this.sessionID = sessionID;
        dataFile = new File(activity.getApplicationContext().getFilesDir() + "/data", sessionID + ".txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                arr.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
