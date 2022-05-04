package com.project.smartshoes.datahandler;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DataRecordingSession {
    private long startTime;
    private long endTime;
    private AppCompatActivity activity;
    private ActivityRecorder recorder;
    public String sessionID;

    public DataRecordingSession(AppCompatActivity activity){
        this.activity = activity;
        recorder = new ActivityRecorder(); //TODO: create ActivityRecorder class
    }

    public void startRecording(){
        startTime = System.currentTimeMillis();
        sessionID = String.valueOf(startTime);
        recorder.start(sessionID);
    }

    public void stopRecording(){
        endTime = System.currentTimeMillis();
        recorder.stop();
    }
}
