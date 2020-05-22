package com.example.maptest;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TempService extends Service {
    NotificationManager notificationManager;
    Context context;
    public static final String TAG = "TempService";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"---------------Temp Service Started--------------------");
        super.onStartCommand(intent, flags, startId);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"---------------Temp Service Destroyed--------------------");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"---------------Temp Service onBind--------------------");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.getActiveNotifications();
        }
        return null;
    }
}
