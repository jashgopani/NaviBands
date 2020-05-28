package com.example.maptest;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.maptest.Constants.DIRECTION;
import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.JOB_DONE;
import static com.example.maptest.Constants.PIXEL_DATA;

public class PixelProcessingService extends JobIntentService {
    private static final String TAG = "PixelProcessingService";

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, PixelProcessingService.class, 123, work);
    }

    //remove rgb channels from bitmap
    private static Bitmap turnBinary(Bitmap origin) {
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork: Handling work");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        Icon ic = (Icon)intent.getParcelableExtra("icon");
        if(ic==null){
            Log.d(TAG, "onHandleWork: Icon Null , Stopping work");
            return;
        }
        Drawable d = ic.loadDrawable(this);
        BitmapDrawable icon = (BitmapDrawable) d;

        //working with Bitmap
        Bitmap cb = turnBinary(icon.getBitmap());//remove color channels for faster calc
        int width = cb.getWidth();
        int height = cb.getHeight();

        //store it in PixelWrapper for comparisons
        PixelWrapper p = new PixelWrapper();

        //get pixels of Bitmap first
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int temp = cb.getPixel(i, j);
                int index = i * width + j;
                p.pixels.add(temp);
            }
        }

        //calculate the rleString for faster comparison
        p.compressPixels();

        //get the direction name
        String direction = IconMap.icons.get(p.compressed);

        //broadcast job done intent
        Log.d(TAG, "onHandleWork: Workdone "+ direction);
        broadcastIntent(DIRECTION,direction);
    }
    
    private void broadcastIntent(String key,String value){
        Intent jobDoneIntent = new Intent(JOB_DONE);
        jobDoneIntent.putExtra(key,value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(jobDoneIntent);
    }

    @Override
    public boolean onStopCurrentWork() {
        Log.d(TAG, "onStopCurrentWork: Work will be stopped");
        broadcastIntent(PIXEL_DATA,ICON_NULL);
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
