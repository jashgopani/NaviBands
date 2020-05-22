package com.example.maptest;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationMonitor extends NotificationListenerService {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn != null) {
            String pack = sbn.getPackageName();
            String ticker = sbn.getNotification().tickerText == null ? "TICKER_TEXT_NOT_FOUND" : sbn.getNotification().tickerText.toString();
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text") == null ? "ANDROID_TEXT_NOT_FOUND" : extras.getCharSequence("android.text").toString();
            Notification notif = sbn.getNotification();

            Icon largeIcon = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                largeIcon = notif.getLargeIcon();

            }

            Log.i("Ticker", ticker);
            Log.i("Icon", largeIcon == null ? "ICON_NULL" : largeIcon.toString());
            //            Log.i("Text",text);

            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("ticker", ticker);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);

            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        } else {
            Log.i("MSG", "SBN Null hai");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }
}
