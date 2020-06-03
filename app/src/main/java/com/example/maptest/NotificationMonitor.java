package com.example.maptest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.MAPS_PACKAGE;
import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;
import static com.example.maptest.Constants.REROUTING;

public class NotificationMonitor extends NotificationListenerService {

    private static final String TAG = "NotificationMonitor";
    static Drawable currIcon = null;
    Context context;
    String notificationTitle = "";//title of previous notification
    HashSet<String> titleSet;

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "onListenerConnected: Connected");
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        Log.d(TAG, "onListenerDisconnected: Disconnected");
        super.onListenerDisconnected();
    }

    public static Drawable getIconResource() {
        return currIcon;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        titleSet = new HashSet<String>();
    }

    @Override
    public void onNotificationPosted(@NonNull StatusBarNotification sbn) {
        if (sbn != null) {
            String pack = sbn.getPackageName();
            //Only process notifications from google maps app
            if (pack.equals(MAPS_PACKAGE)) {
                //Get the notification object
                Notification notification = sbn.getNotification();

                //To get the title and the Description text
                Bundle extras = notification.extras;

                //Extracting title, text and icon
                String title = extras.getString("android.title") == null ? "ANDROID_TITLE_NOT_FOUND" : extras.getString("android.title");
                String text = extras.getString("android.text") == null ? "ANDROID_TEXT_NOT_FOUND" : extras.getString("android.text");
                Icon icon = notification.getLargeIcon();
                String type = REROUTING.equals(title) ? REROUTING : title;

                if (!notificationTitle.equals(title)) {
                    notificationTitle = title;
                    //create an intent object for broadcasting
                    Intent msgrcv = createNotificationIntent(type,title,text,icon);

                    //broadcast notification intent
                    Log.d(TAG, "onNotificationPosted: title : "+title);
                    Log.d(TAG, "onNotificationPosted: text : "+text);
                    Log.d(TAG, "onNotificationPosted: type : "+type);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                }
            }


        }
    }

    Intent createNotificationIntent(String type, String title, String text, Icon icon) {
        Intent intent = new Intent(NOTIFICATION_RECEIVED)
                .putExtra("type", type)
                .putExtra("title", title)
                .putExtra("text", text);
        if (!REROUTING.equals(type)) {
            intent.putExtra("icon", icon);
        }else{
            Log.d(TAG, "createNotificationIntent: Icon NULL");
        }
        return intent;
    }
}
