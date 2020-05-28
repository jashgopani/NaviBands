package com.example.maptest;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;

public class NotificationMonitor extends NotificationListenerService {
    public static final String MAPS_PACKAGE = "com.google.android.apps.maps";
    private static final String TAG = "NotificationMonitor";
    static Drawable currIcon = null;
    Context context;
    String notificationTitle = "";

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

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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

                //Extracting title and text
                String title = extras.getString("android.title") == null ? "ANDROID_TITLE_NOT_FOUND" : extras.getString("android.title");
                String text = extras.getString("android.text") == null ? "ANDROID_TEXT_NOT_FOUND" : extras.getString("android.text");

                //Extracting the Icon
                IconWrapper largeIcon = new IconWrapper();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Get the icon object and convert it to Drawable format if not null
                    Icon temp = notification.getLargeIcon();

                    if (temp != null) {
                        largeIcon.setIcon(temp);
                        currIcon = largeIcon.getIcon().loadDrawable(getApplicationContext());
                    }
                }

                //We have all the details, send the details via intent

                //Create an intent and add details
                Intent msgrcv = new Intent(NOTIFICATION_RECEIVED);
                msgrcv.putExtra("title", title);
                msgrcv.putExtra("text", text);
                msgrcv.putExtra("icon", largeIcon.getIcon());

                if (!notificationTitle.equals(title)) {
                    notificationTitle = title;
                    Log.d(TAG, "onNotificationPosted: "+title+" | "+text);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                }
            }


        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

}
