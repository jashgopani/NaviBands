package com.example.maptest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
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

import static com.example.maptest.Constants.CSV_FILENAME;
import static com.example.maptest.Constants.DIRECTION;
import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.PIXEL_DATA;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static String PATH = null;
    private static Context context;
    ToggleButton toggleMonitoringBtn, toggleDevModeBtn;
    Button debugFileBtn;
    TextView logtv, statustv;
    ImageView iconiv;
    boolean devMode, monitoringMode;
    String filename;
    BroadcastReceiver directionsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            String direction = intent.getStringExtra(DIRECTION);
            String compressed = intent.getStringExtra(PIXEL_DATA);

            String newData = title + "\n" + text + "\n" + direction;
            String oldData = logtv.getText().toString();

            logtv.setText(newData + oldData);

            //convert icon to drawable
            Icon ic = intent.getParcelableExtra("icon");
            Drawable d = ic.loadDrawable(context);
            iconiv.setImageDrawable(d);

            //show save option if dev mode on
            if (devMode) {
                showAlertDialog(compressed, d);
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
        PATH = context.getExternalFilesDir(null).getAbsolutePath() + File.separator;
        filename = PATH + CSV_FILENAME;
        monitoringMode = false;
    }

    private void findViews() {
        toggleMonitoringBtn = findViewById(R.id.toggleMonitoringBtn);
        toggleDevModeBtn = findViewById(R.id.toggleDevModeBtn);
        debugFileBtn = findViewById(R.id.debugFileBtn);
        logtv = findViewById(R.id.logtv);
        statustv = findViewById(R.id.statustv);
        iconiv = findViewById(R.id.imageIcon);
    }

    private void addEventListeners() {
        toggleMonitoringBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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

        toggleDevModeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //update devMode
                devMode = isChecked;
                boolean res = false;

                if (isChecked) {
                    //monitoring is on
                    res = openFileStream();
                    Log.d(TAG, "toggleDevModeBtn: Open file stream : " + res);
                } else {
                    //monitoring is off
                    res = closeFileStream();
                    Log.d(TAG, "toggleDevModeBtn: Close file stream : " + res);
                }
            }
        });

        debugFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //perform file write operation
                debugFileOperation();
            }
        });
    }

    private void setStatustv() {
        statustv.setText(monitoringMode ? "Monitoring ON" : "Monitoring OFF");
    }

    private void getPermissions() {
        //check storage permissions & ask for it if not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        //check location permissions & ask for it if not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        //Check for Notification access
        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            //service is enabled do something
            Log.d(TAG, "Notification access enabled");
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
        statustv.setText(R.string.monitoring_off);
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

    private boolean openFileStream() {
        return CSVUtilities.openCSVFileWriter(context, PATH, filename, true);
    }

    private boolean closeFileStream() {
        return CSVUtilities.closeCSVFileWriter(context);
    }

    private void debugFileOperation() {
        try {
            openFileStream();
            CSVUtilities.writeToCSVFile(new String[]{new Date().toString(), "" + R.string.debug_file_success});
            closeFileStream();
            Toast.makeText(context, R.string.debug_file_success, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "debugFileOperation: SUCCESS");
        } catch (Exception e) {
            Toast.makeText(context, R.string.debug_file_failure, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "debugFileOperation: FAILURE");
        }
    }

    private void showAlertDialog(String compressedValue, Drawable icon) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("New Icon");
        alertDialog.setMessage("Enter Icon Name");
        alertDialog.setCancelable(false);

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(icon);

        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String iconName = input.getText().toString().trim().toUpperCase();
                        iconName = iconName.length() > 0 ? iconName : "unknown";
                        if (iconName.length() > 0) {
                            //write to file
                            IconMap.icons.put(compressedValue, iconName);
                            CSVUtilities.writeToCSVFile(new String[]{compressedValue, iconName});
                        } else {

                            Toast.makeText(context, "Enter valid name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.show();
    }
}