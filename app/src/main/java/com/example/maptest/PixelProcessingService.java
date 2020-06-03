package com.example.maptest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.DIRECTION_KNOWN;
import static com.example.maptest.Constants.DIRECTION_UNKNOWN;
import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.PIXEL_DATA;
import static com.example.maptest.Constants.REROUTING;
import static com.example.maptest.MainActivity.history;

public class PixelProcessingService {
    private static final String TAG = "PixelProcessingService";

    //remove rgb channels from bitmap
    private static Bitmap turnBinary(Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        Bitmap bitmap = origin.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = bitmap.getPixel(i, j);
                int A = (int)((color & 0xff)<< 24);
                if(A>0)
                Log.d(TAG, "turnBinary: i,j : "+A);
                bitmap.setPixel(i, j, A);
            }
        }
        return bitmap;
    }

    //get Direction from icons
    protected static void getDirection(Context context, @NonNull Intent intent) {

        Log.d(TAG, "getDirection: Inside PixelProcessingService");
        //return if Map is rerouting
        if (REROUTING.equals(intent.getStringExtra("type"))){
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DIRECTION_BROADCAST).putExtra("type", REROUTING));
            return;
        }

        //terminate if icon is null
        Icon ic = (Icon) intent.getParcelableExtra("icon");
        if (ic == null) {
            Log.d(TAG, "getDirection: Icon Null , Stopping work");
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DIRECTION_BROADCAST).putExtra("type", ICON_NULL));
            return;
        }

        Log.d(TAG, "getDirection: Starting processing of BITMAP");
        //Perform operations and calculate processing time
        long start_time = System.nanoTime();

        //extract text elements
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        //convert icon to bitmap
        Drawable d = ic.loadDrawable(context);
        BitmapDrawable icon = (BitmapDrawable) d;

        //Bitmap processing starts
        Bitmap cbOriginal = turnBinary(icon.getBitmap());//remove color channels for faster calc
        int width = cbOriginal.getWidth();
        int height = cbOriginal.getHeight();

        float scaleWidth = 100f / width;
        float scaleHeight = 100f / height;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight, width / 2f, height / 2f);

        Bitmap cb = Bitmap.createBitmap(cbOriginal, 0, 0, width, height, matrix, true);
        width = cb.getWidth();
        height = cb.getHeight();
        Log.d(TAG, "getDirection: " + width + "x" + height);
        //store it in PixelWrapper for comparisons
        PixelWrapper p = new PixelWrapper();

        //get pixels of Bitmap first
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int temp = cb.getPixel(i, j);
                p.pixels.add(temp);
            }
        }

        //calculate the rleString for faster comparison
        p.compressPixels();

        //get the direction name
        String direction = IconMap.icons.get(p.compressed);

        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;
        Log.d(TAG, "getDirection: Processing time " + difference);

        String imageName = "NA";
        if(!history.add(p.compressed))imageName = storeImage(cb, context);

        //broadcast job done intent
        Log.d(TAG, "getDirection: Workdone " + direction);
        Intent jobDoneIntent = new Intent(DIRECTION_BROADCAST);
        if (direction == null) {
            jobDoneIntent.putExtra("type", DIRECTION_UNKNOWN);
        } else {
            jobDoneIntent.putExtra("type", DIRECTION_KNOWN);
            jobDoneIntent.putExtra("direction", direction);
        }
        jobDoneIntent.putExtra("title", title);
        jobDoneIntent.putExtra("text", text);
        jobDoneIntent.putExtra("icon", ic);
        jobDoneIntent.putExtra(PIXEL_DATA, p.compressed);
        jobDoneIntent.putExtra("filename",imageName);
        //broadcast the intent
        LocalBroadcastManager.getInstance(context).sendBroadcast(jobDoneIntent);
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
        String timeStamp = (new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()));
        File mediaFile;
        String mImageName = timeStamp + ".JPG";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    //Write the bitmap to filesystem as a image
    public static String storeImage(Bitmap image, Context context) {
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return "NA";
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.close();
            Log.i(TAG, "Image saved successfully");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        return pictureFile.getName();
    }

}
