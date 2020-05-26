package com.example.maptest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TempService extends Service {
    public static final String TAG = "TempService";
    private static int notificationId = 69;
    String CHANNEL_ID = "MAP_MONITOR";
    boolean monitoringStatus = false;
    NotificationManager notificationManager;
    Context context;

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // To recieve intents from service, perform the actions of onRecieve method
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
//            String old = logtv.getText().toString();

            String not = "\n{title : " + title + "\ntext :" + text + "\n";

            //update the text on screen
//            logtv.setText(old+not);

            if (intent != null) {
                //getting current BitmapDrawable
                Drawable iconDrawable = NotificationMonitor.getIconResource();
                BitmapDrawable icon = (BitmapDrawable) iconDrawable;

                //working with Bitmap
                Bitmap cb = turnBinary(icon.getBitmap());//remove color channels for faster calc
                int width = cb.getWidth();
                int height = cb.getHeight();
                int[] pixels = new int[width * height];//create an array to store pixel values
                int zeroCounter = 0;

                //store it in PixelWrapper for comparisons
                PixelWrapper p = new PixelWrapper();

                //get pixels of Bitmap first
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        int temp = cb.getPixel(i, j);
                        int index = i * width + j;
                        pixels[index] = temp;
                        p.pixels.add(pixels[index]);
                        if (temp == 0) zeroCounter++;
                    }
                }

                //calculate the rleString for faster comparison
                p.compressPixels();
                

                System.out.println("--------------------------Pixels Start--------------------------------");
                System.out.println("Zeros : " + zeroCounter);
                System.out.println(p);

//                if (largeIcons.add(p.compressed)) {//if new icon detected
//                    //display the icon
//                    imageIcon.setImageDrawable(icon);
//
//                    //for debugging
//                    System.out.println("Bitmap is unique");
//
//                    //Unregister reciever for the time being
//                    unregisterBroadcastReceiver();
//
//                    //save image as PNG for backup
//                    storeImage(cb);
//
//                    //show dialog and ask for label name
//                    showAlertDialog(p.compressed, icon);
//
//
//                } else {
//                    System.out.println("Bitmap already exists");
//                }
//
//                System.out.println("HashSet Size : " + largeIcons.size());
//                System.out.println("--------------------------Pixels End--------------------------------");

            }
        }
    };

    //remove rgb channels from bitmap
    public Bitmap turnBinary(Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        Bitmap bitmap = origin.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int col = bitmap.getPixel(i, j);
                int alpha = (col & 0xFF000000) >> 24;
                int red = (col & 0x00FF0000);
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF) >> 16;
                int newColor = alpha | blue | green | red;
                bitmap.setPixel(i, j, newColor);
            }
        }
        return bitmap;
    }

    private void registerBroadcastReceiver(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, new IntentFilter("Msg"));
//        statustv.setText("Monitoring ON");
        Toast.makeText(context, "Monitoring ON", Toast.LENGTH_SHORT).show();
    }

    private void unregisterBroadcastReceiver(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(onNotice);
//        statustv.setText("Monitoring OFF");
        Toast.makeText(context, "Monitoring OFF", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        createNotificationChannel();
        registerBroadcastReceiver(context);
    }

    private void startNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(monitoringStatus ? "Monitoring on" : "Monitoring off")
                .setContentText("")
                .setOngoing(true)
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColorized(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("Map Monitoring");
            String description = "Monitor google map navigation instructions";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "---------------Temp Service Started--------------------");
        startNotification();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "---------------Temp Service Destroyed--------------------");
        notificationManager.cancel(notificationId);
        unregisterBroadcastReceiver(context);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "---------------Temp Service onBind--------------------");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.getActiveNotifications();
        }
        return null;
    }
}
