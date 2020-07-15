package com.example.maptest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jashgopani.github.io.mibandsdk.MiBand;
import jashgopani.github.io.mibandsdk.models.CustomVibration;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.DIRECTION_KNOWN;
import static com.example.maptest.Constants.DIRECTION_UNKNOWN;
import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.REROUTING;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static Context context;
    ToggleButton toggleMonitoringBtn;
    TextView logtv, statustv,thresholdtv, bandMacTv;
    TextView bandConnectionStatusTv,bandBatteryStatusTv,bandChargingStatusTv;
    Button gotoConnectBtn;
    SeekBar thresholdSb;
    private int currentThreshold = 5;
    boolean monitoringMode;
    MiBand miband;
    int currentBattery = -1,selectedVibrationMode = 0;
    String chargingStatus;
    Spinner vibrateSpinner;
    ArrayAdapter vibrateOnlyAdapter;
    private CompositeDisposable disposables;

    String vibrateModesArray[] ;

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
                    miband.vibrate(getPatternFromDirection(""));
                } else if (DIRECTION_KNOWN.equals(type)) {
                    Log.d(TAG, "onReceive: Intent is DIRECTION_KNOWN");
                    String direction = intent.getStringExtra("direction");
                    int distance = -1;
                    //get unit and distance from title
                    if(title.indexOf("-")>0){
                        String t = title.substring(0,title.indexOf("-")).trim().toLowerCase();
                        char[] c = t.toCharArray();
                        if(!(c[t.length()-2]=='k')){
                            try {
                                distance = Integer.parseInt(t.substring(0,t.length()-2));
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            if(distance<=currentThreshold){
                                String msg = direction+" in "+distance+"m";
                                updateMonitoringService(title,msg);
                                miband.vibrate(getPatternFromDirection(direction));
                                Toast.makeText(context, "<< Naviband Vibrates >>", Toast.LENGTH_SHORT).show();
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
    private Disposable batteryDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: called");
        init();
    }


    private void init() {
        findViews();
        initializeVariables();
        addEventListeners();
        getPermissions();
        setStatustv();
        checkDeviceCompatibility();
    }

    //methods used by init
    private void checkDeviceCompatibility() {
        //get The bluetooth adapter
        BluetoothAdapter bluetoothAdapter;
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        //check bluetooth enabled or not
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,1);
        }
    }

    private void initializeVariables() {
        context = getApplicationContext();
        monitoringMode = false;

        //seek bar and its related text view
        thresholdSb.setProgress(currentThreshold);
        thresholdtv.setText(currentThreshold+"m");

        //For miband
        miband = MiBand.getInstance(MainActivity.this);

        //vibrate mode speaker
        vibrateModesArray = new String[]{
                "LEFT + RIGHT",
                "LEFT ONLY",
                "RIGHT ONLY"
        };
        //setup adapter
        vibrateOnlyAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,vibrateModesArray);
        vibrateOnlyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vibrateSpinner.setAdapter(vibrateOnlyAdapter);
    }

    private void findViews() {
        toggleMonitoringBtn = findViewById(R.id.toggleMonitoringBtn);
        logtv = findViewById(R.id.logtv);
        statustv = findViewById(R.id.statustv);
        thresholdSb = findViewById(R.id.thresholdSeek);
        thresholdtv = findViewById(R.id.thresholdTv);
        gotoConnectBtn = findViewById(R.id.gotoConnectBtn);
        bandMacTv = findViewById(R.id.bandMacTv);
        bandConnectionStatusTv = findViewById(R.id.bandConnectedStatusTv);
        bandBatteryStatusTv = findViewById(R.id.bandBatteryStatusTv);
        bandChargingStatusTv = findViewById(R.id.bandChargingStatusTv);
        vibrateSpinner = findViewById(R.id.vibrateOnlySpinner);
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
                    miband.vibrate(CustomVibration.generatePattern("600",","));
                } else {
                    //monitoring is off
                    statustv.setText(R.string.monitoring_off);
                    stopMonitoringService();
                    unregisterReceiver();
                }
            }
        });

        thresholdSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = MathUtils.clamp(progress, 5, 100);
                currentThreshold = roundTo(progress,5);
                thresholdtv.setText(currentThreshold+"m");
                thresholdtv.setTextColor(Color.RED);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        gotoConnectBtn.setOnClickListener(v->{
            goToConnectActivity(false);
        });

        vibrateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVibrationMode = position;
                Toast.makeText(MainActivity.this, "Band will vibrate for "+vibrateModesArray[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
                Log.d(TAG, "onNothingSelected: Done nothing");
            }
        });

    }

    private void setStatustv() {
        statustv.setText(monitoringMode ? R.string.monitoring_on : R.string.monitoring_off);
    }

    private void getPermissions() {
        String permissions[] ={
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(MainActivity.this, permissions, 0);

        //Check for Notification access
        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            //service is enabled do something
            Log.d(TAG, "Notification access already enabled");
        } else {
            //service is not enabled try to enabled by calling...
            startActivity(new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }


        verifyBandAvailability();
    }

    private void verifyBandAvailability() {
        //if device is null, goto connect activity
        if(!miband.isPaired())goToConnectActivity(true);
        else Log.d(TAG, "verifyBandAvailability: "+miband.getDevice());
    }

    private void goToConnectActivity(boolean withResult) {
        Intent intent = new Intent(MainActivity.this,BandConnectActivity.class);
        if(!withResult)startActivity(intent);
        else startActivityForResult(intent.putExtra("requestCode", 69),69);
    }

    private int roundTo(int i, int r) {
        r = Math.max(1,r);
        return Math.max(r * (Math.round(i / r)),0);
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

    private Integer[] getPatternFromDirection(String d) {
        Integer noVibration[] = new Integer[]{};
        if(Directions.isUturn(d)) return CustomVibration.generatePattern(300,100,4);
        else if(Directions.isLeft(d)) return (selectedVibrationMode!=2)?CustomVibration.LEFT_PULSE:noVibration;
        else if(Directions.isRight(d)) return (selectedVibrationMode!=1)?CustomVibration.RIGHT_PULSE:noVibration;
        else switch (d) {
                case Directions.STRAIGHT:
                    return noVibration;
                case Directions.ALTERNATE:
                    return CustomVibration.FROWN;
                case Directions.ARRIVED:
                    return CustomVibration.generatePattern("600",",");
                default:
                    return CustomVibration.generatePattern("600",",");
            }
    }
    private void updateBandStats(){
        boolean paired = miband.isPaired();
        bandMacTv.setText(miband.getDevice().toString());
        if(paired){
            bandConnectionStatusTv.setText(MiBand.getStatus(MiBand.PAIRED));
            bandMacTv.setTextColor(Color.GREEN);
            bandBatteryStatusTv.setText(currentBattery==-1?"---":String.valueOf(currentBattery));
            bandChargingStatusTv.setText(chargingStatus==null?"---":String.valueOf(chargingStatus));
        }else{
            bandConnectionStatusTv.setText(MiBand.getStatus(MiBand.DISCONNECTED));
            bandMacTv.setTextColor(Color.RED);
            bandBatteryStatusTv.setText("---");
            bandChargingStatusTv.setText("---");
        }
    }

    /**
     * Retrieve Battery Info and update UI
     */
    private void refreshBatteryInfo(boolean onlyOnce){
        Log.d(TAG, "refreshBatteryInfo: "+miband.isPaired());
        if(miband.isPaired())
            batteryDisposable = miband.getBatteryInfo(5, TimeUnit.MINUTES,onlyOnce)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(info->{
                        Log.d(TAG, "refreshBatteryInfo: "+info);
                        chargingStatus = info.getStatus();
                        currentBattery = info.getLevel();
                        updateBandStats();
                    },err->{
                        Toast.makeText(context, err.getMessage(), Toast.LENGTH_SHORT).show();
                        err.printStackTrace();
                    },()->{
                        Log.d(TAG, "getBatteryInfo: onComplete");
                    });

    }

    private void disconnectAndUnpair() {
        currentBattery = -1;
        batteryDisposable.dispose();
        miband.disconnect(true);
    }

    //for test vibrate buttons
    public void onClick(View v) {
        try {
            switch(v.getId()){
                case R.id.testLeft:
                    miband.vibrate(getPatternFromDirection(Directions.LEFT));
                    break;
                case R.id.testRight:
                    miband.vibrate(getPatternFromDirection(Directions.RIGHT));
                    break;
                case R.id.testStraight:
                    miband.vibrate(getPatternFromDirection(Directions.STRAIGHT));
                    break;
                case R.id.testUturn:
                    miband.vibrate(getPatternFromDirection(Directions.U_LEFT));
                    break;
                case R.id.testAlternate:
                    miband.vibrate(getPatternFromDirection(Directions.ALTERNATE));
                    break;
                case R.id.testArrived:
                    miband.vibrate(getPatternFromDirection(Directions.ARRIVED));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this,ForegroundService.class));
        Log.d(TAG, "onDestroy: Activity destoryed");
        disconnectAndUnpair();
        disposables.clear();
        super.onDestroy();
    }

    //to handle results from other activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==69){//result is from connect activity
            if(resultCode==App.DEVICE_CONNECTED){
                Log.d(TAG, "onActivityResult: Device Connected = "+miband.getDevice());
                miband.vibrate(CustomVibration.SMILE);
                refreshBatteryInfo(true);
            }else if(resultCode == App.DEVICE_DISCONNECTED){
                Log.d(TAG, "onActivityResult: No device connected");
            }else if(resultCode == App.DEVICE_NULL){
                Log.d(TAG, "onActivityResult: No device selected");
            }
            updateBandStats();
        }
    }
}