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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationMonitor extends NotificationListenerService {
    Context context;
    static Drawable currIcon = null;
    String notificationTitle = "";
    public static final String MAPS_PACKAGE = "com.google.android.apps.maps";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn != null) {
            String pack = sbn.getPackageName();
            //Only process notifications from google maps app
            if(pack.equals(MAPS_PACKAGE)){
                //Get the notification object
                Notification notification = sbn.getNotification();

                //To get the title and the Description text
                Bundle extras = notification.extras;

                //Extracting title and text
                String title = extras.getString("android.title")==null?"ANDROID_TITLE_NOT_FOUND":extras.getString("android.title");
                String text = extras.getString("android.text") == null ? "ANDROID_TEXT_NOT_FOUND" : extras.getString("android.text");

                //Extracting the Icon
                IconWrapper largeIcon = new IconWrapper();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Get the icon object and convert it to Drawable format if not null
                    Icon temp = notification.getLargeIcon();

                    if(temp!=null){
                        largeIcon.setIcon(temp);
                        currIcon = largeIcon.getIcon().loadDrawable(getApplicationContext());
                    }
                }

                //We have all the details, send the details via intent

                //Create an intent and add details
                Intent msgrcv = new Intent("Msg");
                msgrcv.putExtra("title", title);
                msgrcv.putExtra("text", text);

                if(!notificationTitle.equals(title)){
                    notificationTitle = title;
                    LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                }
            }


        } else {
            Log.i("MSG", "SBN Null hai");
        }
    }

    public static Drawable getIconResource(){
        return currIcon;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

}
