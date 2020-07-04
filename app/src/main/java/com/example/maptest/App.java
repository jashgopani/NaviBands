package com.example.maptest;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import jashgopani.github.io.mibandsdk.MiBand;

public class App extends Application {
    public static final String CHANNEL_ID = "Process Recieved Notifications";
    protected static Context context = null;
    protected static final int DEVICE_CONNECTED = 167;
    protected static final int DEVICE_DISCONNECTED = 546;
    protected static final int DEVICE_NULL = 938;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        context = getApplicationContext();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Process Recieved Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }

}
