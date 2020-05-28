package com.example.maptest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.maptest.App.CHANNEL_ID;
import static com.example.maptest.Constants.DIRECTION;
import static com.example.maptest.Constants.JOB_DONE;
import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;
import static com.example.maptest.Constants.PIXEL_DATA;
import static com.example.maptest.Constants.REROUTING;

/*
 * This class is for showing notifications while service is running
 * */
public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    private static NotificationReceiver notificationReceiver = null;
    private static JobStatusReceiver jobStatusReceiver = null;

    void vibrateBand(String direction){
        showNotification(this,DIRECTION,direction);
    }

    @Override
    public void onCreate() {
        notificationReceiver = new NotificationReceiver();
        IntentFilter notificationIntentFilter = new IntentFilter(NOTIFICATION_RECEIVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, notificationIntentFilter);

        jobStatusReceiver = new JobStatusReceiver();
        IntentFilter jobStatusIntentFilter = new IntentFilter(JOB_DONE);
        LocalBroadcastManager.getInstance(this).registerReceiver(jobStatusReceiver, jobStatusIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        Log.d(TAG, "onStartCommand: "+title+"  |  "+text);
        showNotification(this,title,text);
        return START_NOT_STICKY;
    }

    private void showNotification(Context context,String title,String text){
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText("Navigating : "+text==null?"":text)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();

        startForeground(69, notification);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroying service");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(jobStatusReceiver);
        super.onDestroy();
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Not using this method");
        return null;
    }

    class NotificationReceiver extends BroadcastReceiver {
        private static final String TAG = "NotificationReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive:̥ "+ TAG);
            if(!REROUTING.equals(intent.getStringExtra("type")))
                PixelProcessingService.enqueueWork(context,intent);
        }
    }

    class JobStatusReceiver extends BroadcastReceiver{
        private static final String TAG = "JobStatusReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            String direction = intent.getStringExtra(DIRECTION)==null?"UNKNOWN":intent.getStringExtra(DIRECTION);
            Log.d(TAG, "onReceive: Direction Identified as" +direction);
        }
    }

}
