package com.example.maptest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    Button startServiceBtn, stopServiceBtn;
    ImageView imageIcon;
    TextView statustv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isPlayServicesInstalled()) {
            init();
        }


        //TODO:Check internet connctivity
        //TODO:Check Gps Enabled
        // Todo:check bluetooth enabled (future)
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        // whenever the broadcast_reciever recieves a notification details from service, perform the actions of onRecieve method

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");

            Log.i(TAG, "\npackage : " + pack + "\ntitle : " + title + "\ntext :" + text);
        }
    };


    private void init() {
        Button openMapsBtn = findViewById(R.id.openMapsBtn);
        startServiceBtn = findViewById(R.id.startServiceBtn);
        stopServiceBtn = findViewById(R.id.stopServiceBtn);
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        statustv = (TextView) findViewById(R.id.statustv);

        openMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });

        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statustv.setText("Monitoring onn");
                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(onNotice, new IntentFilter("Msg"));
                Toast.makeText(MainActivity.this, "Monitoring on", Toast.LENGTH_SHORT);
                Log.i(TAG, "clicked start");

            }
        });

        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statustv.setText("Monitoring off");
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(onNotice);
            }
        });
    }

    @SuppressLint("ShowToast")
    public boolean isPlayServicesInstalled() {
        Log.d(TAG, "checking version of playservices");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "installed");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(MainActivity.this, "Bhul jaa , Services error", Toast.LENGTH_SHORT);
        }
        return false;
    }

}
