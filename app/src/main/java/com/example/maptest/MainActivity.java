package com.example.maptest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.maptest.Constants.CSV_FILENAME;
import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.DIRECTION_KNOWN;
import static com.example.maptest.Constants.DIRECTION_UNKNOWN;
import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.ENCODED_DATA;
import static com.example.maptest.Constants.REROUTING;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static Context context;
    ToggleButton toggleMonitoringBtn;
    TextView logtv, statustv;
    boolean monitoringMode;

    final BroadcastReceiver directionsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Processed Intent Received");

            String type = intent.getStringExtra("type");
            if (REROUTING.equals(type)) {
                Log.d(TAG, "onReceive: Intent is REROUTING");
                return;
            } else if (ICON_NULL.equals(type)) {
                Log.d(TAG, "onReceive: Intent is ICON_NULL");
                return;
            } else {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                String newData = title + "\n" + text + "\n";


                if (DIRECTION_UNKNOWN.equals(type)) {
                    Log.d(TAG, "onReceive: Intent is DIRECTION_UNKNOWN");
                    newData += "\n";
                } else if (DIRECTION_KNOWN.equals(type)) {
                    Log.d(TAG, "onReceive: Intent is DIRECTION_KNOWN");
                    String direction = intent.getStringExtra("direction");
                    int distance = -1;
                    //get unit and distance from title
                    if(title.indexOf("-")>0){
                        String t = title.substring(0,title.indexOf("-")).trim().toLowerCase();
                        char[] c = t.toCharArray();
                        if(!(c[t.length()-2]=='k')){
                            distance = Integer.parseInt(t.substring(0,t.length()-2));
                            if(distance<=20){
                                String msg = direction+" in "+distance+"m";
                                updateMonitoringService(title,msg);
                            }else{
                                updateMonitoringService(title,"Navigating..");
                            }
                        }else{
                            updateMonitoringService(title,"Navigating..");
                        }
                    }
                    newData += direction + "\n\n";
                }

                //update UI
                String oldData = logtv.getText().toString();
                logtv.setText(newData + oldData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initializeVariables();
        findViews();
        addEventListeners();
        getPermissions();
        setStatustv();
    }

    private void initializeVariables() {
        context = getApplicationContext();
        monitoringMode = false;
    }

    private void findViews() {
        toggleMonitoringBtn = findViewById(R.id.toggleMonitoringBtn);
        logtv = findViewById(R.id.logtv);
        statustv = findViewById(R.id.statustv);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this,ForegroundService.class));
        super.onDestroy();
    }


    private void addEventListeners() {
        toggleMonitoringBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: Monito");
                monitoringMode = isChecked;
                setStatustv();
                if (isChecked) {
                    //monitoring is on
                    startMonitoringService();
                    registerReceiver();
                } else {
                    //monitoring is off
                    statustv.setText(R.string.monitoring_off);
                    stopMonitoringService();
                    unregisterReceiver();
                }
            }
        });

    }

    private void setStatustv() {
        statustv.setText(monitoringMode ? R.string.monitoring_on : R.string.monitoring_off);
    }

    private void getPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        //Check for Notification access
        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            //service is enabled do something
            Log.d(TAG, "Notification access already enabled");
        } else {
            //service is not enabled try to enabled by calling...
            startActivity(new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }

    private void startMonitoringService() {
        Intent intent = new Intent(context, ForegroundService.class);
        intent.putExtra("title", "NaviBands");
        intent.putExtra("text", "Navigation Mode ON");
        ContextCompat.startForegroundService(context, intent);
        Log.d(TAG, "startMonitoringService: " + R.string.monitoring_on);
    }

    private void updateMonitoringService(String title,String text) {
        Intent intent = new Intent(context, ForegroundService.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        ContextCompat.startForegroundService(context, intent);
        Log.d(TAG, "updateMonitoringService: onStartCommand >> "+title+" | "+text);
        Log.d(TAG, "startMonitoringService: " + R.string.monitoring_on);
    }


    private void stopMonitoringService() {
        stopService(new Intent(context, ForegroundService.class));
        statustv.setText(R.string.monitoring_off);
        Log.d(TAG, "stopMonitoringService: " + R.string.monitoring_off);
    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(directionsReceiver, new IntentFilter(DIRECTION_BROADCAST));
        Log.d(TAG, "registerReceiver: DIRECTION_BROADCAST registered");
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(directionsReceiver, new IntentFilter(DIRECTION_BROADCAST));
        Log.d(TAG, "unregisterReceiver: DIRECTION_BROADCAST unregistered");
    }

}