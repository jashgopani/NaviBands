package com.example.maptest;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.maptest.Constants.CSV_FILENAME;
import static com.example.maptest.Constants.DEBUG_PATH;
import static com.example.maptest.Constants.DIRECTION;
import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.JOB_DONE;
import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;
import static com.example.maptest.Constants.PIXEL_DATA;
import static com.example.maptest.Constants.PROCESSED_NOTIFICATION;
import static com.example.maptest.Constants.REROUTING;

public class PixelProcessingService{
    private static final String TAG = "PixelProcessingService";

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

    //Main processing of Icons
    protected static Intent getDirection(Context context, @NonNull Intent intent) {
        if (REROUTING.equals(intent.getStringExtra("type")))
            return new Intent(DIRECTION_BROADCAST).putExtra("type",REROUTING);

        Log.d(TAG, "getDirection: Handling work");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        Icon ic = (Icon)intent.getParcelableExtra("icon");
        if(ic==null){
            Log.d(TAG, "getDirection: Icon Null , Stopping work");
            return null;
        }

        Drawable d = ic.loadDrawable(context);
        BitmapDrawable icon = (BitmapDrawable) d;

        long start_time = System.nanoTime();

        //working with Bitmap
        Bitmap cbOriginal = turnBinary(icon.getBitmap());//remove color channels for faster calc
        Log.d(TAG, "getDirection: Original Image ID : "+cbOriginal.getGenerationId());
        int width = cbOriginal.getWidth();
        int height = cbOriginal.getHeight();
        float scaleWidth = width/100f;
        float scaleHeight = height/100f;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth,scaleHeight,width/2f,height/2f);

        Bitmap cb = Bitmap.createBitmap(cbOriginal, 0, 0,
                100,100, matrix, true);

        storeImage(cb,context);
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

        //debug write array of pixels to file
        CSVUtilities.openCSVFileWriter(context,DEBUG_PATH,DEBUG_PATH+CSV_FILENAME,true);
        CSVUtilities.writeToCSVFile(new String[]{title+" | "+cb.getGenerationId(),p.pixels.toString()});
        CSVUtilities.closeCSVFileWriter(context);

        //get the direction name
        String direction = IconMap.icons.get(p.compressed);

        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;
        Log.d(TAG, "getDirection: Processing time "+ difference);

        //broadcast job done intent
        Log.d(TAG, "getDirection: Workdone "+ direction);
        Intent jobDoneIntent = new Intent(DIRECTION_BROADCAST);
        jobDoneIntent.putExtra("title",title);
        jobDoneIntent.putExtra("text",text);
        jobDoneIntent.putExtra(DIRECTION,direction);
        jobDoneIntent.putExtra(PIXEL_DATA,p.compressed);
        jobDoneIntent.putExtra("icon",ic);
        jobDoneIntent.putExtra(ICON_NULL,ic==null?"true":"false");
        jobDoneIntent.putExtra("type",NOTIFICATION_RECEIVED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(jobDoneIntent);
        return jobDoneIntent;
    }

    //Create a File for saving an image or video
    private static File getOutputMediaFile(Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
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
        String timeStamp = "100x100"+(new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()));
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    //Write the bitmap to filesystem as a image
    private static void storeImage(Bitmap image,Context context) {
        Log.d(TAG, "storeImage: Scaled Image Id : "+image.getGenerationId());
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.i(TAG, "Image saved successfully");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
}
