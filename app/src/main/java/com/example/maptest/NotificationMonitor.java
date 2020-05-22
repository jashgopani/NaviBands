package com.example.maptest;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationMonitor extends NotificationListenerService {
    Context context;
    static Drawable currIcon = null;
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

                //serializeIcon
                serializeIcon(largeIcon);

                LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
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

    private void serializeIcon(IconWrapper object){

        File filename = getOutputMediaFile();

        // Serialization
        try
        {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(object);

            out.close();
            file.close();

            System.out.println(filename+" has been serialized\n");
        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date())+"ICON.ser";
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}
