package com.example.maptest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.maptest.Constants.CSV_FILENAME;
import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.DIRECTION_KNOWN;
import static com.example.maptest.Constants.DIRECTION_UNKNOWN;
import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.PIXEL_DATA;
import static com.example.maptest.Constants.REROUTING;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static String PATH = null;
    public static HashSet<String> history;
    public static HashSet<UnknownDrawables> unknowns;
    List<UnknownDrawables> unknownsList ;
    private static Context context;
    ToggleButton toggleMonitoringBtn;
    Button debugFileBtn;
    TextView logtv, statustv,iconCounttv;
    ImageView iconiv;
    boolean monitoringMode;
    String filename;

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
            } else {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                String compressed = intent.getStringExtra(PIXEL_DATA);
                String filename = intent.getStringExtra("filename");
                String newData = title + "\n" + text + "\n";

                //convert icon to drawable
                Icon ic = intent.getParcelableExtra("icon");
                Drawable d = ic.loadDrawable(context);

                history.add(compressed);
                if (DIRECTION_UNKNOWN.equals(type)) {
                    Log.d(TAG, "onReceive: Intent is DIRECTION_UNKNOWN");
                    newData += "\n";
                    unknowns.add(new UnknownDrawables(d, compressed, filename));
                    iconCounttv.setText("Icons Detected : "+unknowns.size());
                } else if (DIRECTION_KNOWN.equals(type)) {
                    Log.d(TAG, "onReceive: Intent is DIRECTION_KNOWN");
                    String direction = intent.getStringExtra("direction");
                    newData += direction + "\n\n";
                }

                //update UI
                String oldData = logtv.getText().toString();
                logtv.setText(newData + oldData);
                iconiv.setImageDrawable(d);
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
        Constants.DEBUG_PATH = PATH;
        filename = PATH + CSV_FILENAME;
        monitoringMode = false;
        history = new HashSet<>();
        unknowns = new HashSet<>();
    }

    private void findViews() {
        toggleMonitoringBtn = findViewById(R.id.toggleMonitoringBtn);
        debugFileBtn = findViewById(R.id.debugFileBtn);
        logtv = findViewById(R.id.logtv);
        statustv = findViewById(R.id.statustv);
        iconiv = findViewById(R.id.imageIcon);
        iconCounttv = findViewById(R.id.iconCounttv);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this,ForegroundService.class));
        writeIconsMapToCSV();
        super.onDestroy();

    }

    private void writeIconsMapToCSV() {
        openFileStream();
        //writing HashMap to csv file
        for (Map.Entry element : IconMap.icons.entrySet()) {
            CSVUtilities.writeToCSVFile(new String[]{element.getKey().toString(), element.getValue().toString()});
        }
        closeFileStream();
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

        debugFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //perform file write operation
                debugFileOperation();
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
        Log.d(TAG, "startMonitoringService: " + R.string.monitoring_on);
    }

    private void stopMonitoringService() {
        stopService(new Intent(context, ForegroundService.class));
        statustv.setText(R.string.monitoring_off);
        unknownsList = new ArrayList<>(unknowns);
        labelUnknowns(0);
        writeIconsMapToCSV();
        Log.d(TAG, "stopMonitoringService: " + R.string.monitoring_off);
    }

    private void labelUnknowns(int index) {
        if (index >= unknowns.size()) return;
        UnknownDrawables u = unknownsList.get(index);
        showAlertDialog(u.filename,u.compressed, u.drawable, MainActivity.this, index);
    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(directionsReceiver, new IntentFilter(DIRECTION_BROADCAST));
        openFileStream();
        Log.d(TAG, "registerReceiver: DIRECTION_BROADCAST registered");
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(directionsReceiver, new IntentFilter(DIRECTION_BROADCAST));
        closeFileStream();
        Log.d(TAG, "unregisterReceiver: DIRECTION_BROADCAST unregistered");
    }

    private boolean openFileStream() {
        return CSVUtilities.openCSVFileWriter(MainActivity.this, PATH, filename, true);
    }

    private boolean closeFileStream() {
        return CSVUtilities.closeCSVFileWriter(context);
    }

    private void debugFileOperation() {
        try {
            boolean o = openFileStream();
            CSVUtilities.writeToCSVFile(new String[]{new Date().toString(), "" + getResources().getString(R.string.debug_file_success)});
            boolean c = closeFileStream();
            boolean r = o & c;
            Toast.makeText(context, getResources().getString(R.string.debug_file_success) + (r ? "YES" : "NO"), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "debugFileOperation: SUCCESS : " + (r ? "YES" : "NO"));
        } catch (Exception e) {
            Toast.makeText(context, getResources().getString(R.string.debug_file_failure), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "debugFileOperation: FAILURE");
        }
    }

    private void showAlertDialog(String filename,String compressedValue, Drawable icon, Context context, int index) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("New Icon " + (index+1) + "/" + unknowns.size());
        alertDialog.setMessage("Enter Icon Name")
        .setCancelable(false);

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

                        //write to file
                        if (iconName.length() > 0) {
                            IconMap.icons.put(compressedValue, iconName);
                            Log.d(TAG, "onClick: AlertDialog : " + iconName);
                        }
                        openFileStream();
                        CSVUtilities.writeToCSVFile(new String[]{filename,compressedValue, iconName});
                        closeFileStream();
                        labelUnknowns(index+1);
                    }
                });

        alertDialog.show();
    }

    class UnknownDrawables {
        Drawable drawable;
        String compressed;
        String filename;

        public UnknownDrawables(Drawable drawable, String compressed, String filename) {
            this.drawable = drawable;
            this.compressed = compressed;
            this.filename = filename;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UnknownDrawables)) return false;
            UnknownDrawables that = (UnknownDrawables) o;

            return drawable.equals(that.drawable) ||
                    compressed.equals(that.compressed);
        }

        @Override
        public int hashCode() {
            return Objects.hash(compressed);
        }
    }

}