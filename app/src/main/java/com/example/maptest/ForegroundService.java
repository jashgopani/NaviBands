package com.example.maptest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.maptest.App.CHANNEL_ID;
import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;
import static com.example.maptest.Constants.PROCESSED_NOTIFICATION;
import static com.example.maptest.Constants.REROUTING;

/*
 * This class is for showing notifications while service is running
 * */
public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    private static NotificationReceiver notificationReceiver = null;
    Context context;

    BroadcastReceiver test = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Test received Notification");
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Created Foreground Service");
        context = getApplicationContext();
        notificationReceiver = new NotificationReceiver();
        IntentFilter notificationIntentFilter = new IntentFilter(NOTIFICATION_RECEIVED);//NOTIFICATION_RECEIVED
        LocalBroadcastManager.getInstance(context).registerReceiver(notificationReceiver, notificationIntentFilter);
        context.registerReceiver(test, notificationIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Started Foreground Service");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        showNotification(this, title, text);
        return START_NOT_STICKY;
    }

    private void showNotification(Context context, String title, String text) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText("Navigating : " + text == null ? "" : text)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();

        startForeground(69, notification);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroying Foreground service");
//        LocalBroadcastManager.getInstance(context).unregisterReceiver(notificationReceiver);
        context.unregisterReceiver(test);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Not using this method");
        return null;
    }

    class NotificationReceiver extends BroadcastReceiver {
        private static final String TAG = "NotificationReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Received NOTIFICATION_RECEIVED intent");

            //process the intent with pixel details and get result intent
            PixelProcessingService.getDirection(context, intent);

        }
    }

}
