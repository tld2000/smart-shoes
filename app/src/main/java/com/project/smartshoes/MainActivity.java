package com.project.smartshoes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;


import com.project.smartshoes.datahandler.ActivityRecordingSession;
import com.project.smartshoes.datahandler.ArduinoDataTransferThread;
import com.project.smartshoes.datahandler.BluetoothConnectionThread;
import com.project.smartshoes.datahandler.Profile;

// Template provided by https://github.com/Hype47/droiduino/tree/master/DroiduinoBluetoothConnection

public class MainActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress;
    private boolean calibrating = false;
    private ActivityRecordingSession recordingSession = null;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ArduinoDataTransferThread arduinoDataTransferThread;
    public static BluetoothConnectionThread bluetoothConnectionThread;
    public static Profile profile;

    public final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Initialization
        final Button buttonConnect = findViewById(R.id.buttonConnect);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final Button measureButtonToggle = findViewById(R.id.buttonToggle);
        final Button calibrationButton = findViewById(R.id.calibration);
        measureButtonToggle.setEnabled(false);

        // loading profile
        profile = new Profile(this);
        if (profile.loadConfig()){
            measureButtonToggle.setVisibility(View.VISIBLE);
            measureButtonToggle.setEnabled(false);
        }


        /*
        Second most important piece of Code. GUI Handler
         */
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                buttonConnect.setEnabled(true);
                                measureButtonToggle.setEnabled(true);
                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    // got message from arduino
                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        switch (arduinoMsg.toLowerCase()){
                            case "start_measuring":
                                recordingSession.startRecording();
                                break;
                            case "stop_measuring":
                                recordingSession.stopRecording();
                                break;
                            case "stop_calibrating": // stopped calibrating
                                // finished receiving calibration data
                                calibrationButton.setEnabled(true);
                                measureButtonToggle.setEnabled(true);
                                calibrating = false;
                                profile.calibrate();
                            default: // get stream of data
                                if (calibrating)
                                    profile.calibrationDataWrite(arduinoMsg); // write to calibration data file
                                else
                                    recordingSession.record(arduinoMsg); // write to session data file
                                    break;
                        }
                        break;
                }
            }
        };

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progress and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            buttonConnect.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            bluetoothConnectionThread = new BluetoothConnectionThread(bluetoothAdapter, handler, deviceAddress);
            bluetoothConnectionThread.start();
            measureButtonToggle.setEnabled(true);
        }

        // Select Bluetooth Device
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to adapter list
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });

        // Button to turn ON/OFF recording on the Adruino
        measureButtonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = null;
                String btnState = measureButtonToggle.getText().toString().toLowerCase();
                switch (btnState){
                    case "start measuring":
                        measureButtonToggle.setText("Stop measuring");

                        // Create a new session if no session exists yet
                        if (recordingSession == null) {
                            recordingSession = new ActivityRecordingSession(MainActivity.this, handler, bluetoothAdapter);
                            recordingSession.loadProfile(profile);
                        } else {
                            switch (recordingSession.getStatus()){
                                case IDLE:
                                    break;
                                case STARTED:
                                    arduinoDataTransferThread.write("stop_measuring");
                                    recordingSession.stopRecording();
                                    recordingSession = new ActivityRecordingSession(MainActivity.this, handler, bluetoothAdapter);
                                    recordingSession.loadProfile(profile);
                                    break;
                                case STOPPED:
                                    recordingSession = new ActivityRecordingSession(MainActivity.this, handler, bluetoothAdapter);
                                    recordingSession.loadProfile(profile);
                                    break;
                            }
                        }
                        cmdText = "<start>";
                        break;
                    case "stop measuring":
                        measureButtonToggle.setText("Start measuring");
                        // Command to turn off LED on Arduino. Must match with the command in Arduino code
                        cmdText = "<stop>";
                        break;

                }
                // Send command to Arduino board
                arduinoDataTransferThread.write(cmdText);
            }
        });


        // button to re-calibrate the device
        calibrationButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // No bluetooth device selected
                if (deviceAddress == null){
                    toolbar.setSubtitle("Please select a bluetooth device to connect to first.");
                    return;
                }

                String cmdText = "<start_calibration>";
                // start the re-calibration process
                calibrationButton.setEnabled(false);
                measureButtonToggle.setEnabled(false);
                profile.createCalibrationFile();
                calibrating = true;
                arduinoDataTransferThread.write(cmdText);
            }
        }));
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (bluetoothConnectionThread != null){
            bluetoothConnectionThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}