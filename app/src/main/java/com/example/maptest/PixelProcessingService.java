package com.example.maptest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.VectorDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.DIRECTION_KNOWN;
import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.REROUTING;

public class PixelProcessingService {
    private static final String TAG = "PixelProcessingService";

    //get alpha pixels of Bitmap as a list
    public static ArrayList<Integer> getAlphaPixels(Bitmap a) {
        ArrayList<Integer> pixels = new ArrayList<>();
        for (int i = 0; i < a.getWidth(); i++)
            for (int j = 0; j < a.getHeight(); j++)
                pixels.add(Color.alpha(a.getPixel(i, j)));

        return pixels;
    }

    //get an alpha bitmap from a resource of size 100x100
    public static Bitmap getComparableBitmap(Context appContext, int resId) {
        //for storing result
        Bitmap bitmap;

        //get drawable from resource id
        Drawable drawable = appContext.getDrawable(resId);
        assert drawable != null;
        drawable = drawable.mutate();

        try {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        } catch (ClassCastException e) {
            VectorDrawable vd = (VectorDrawable) drawable;
            bitmap = Bitmap.createBitmap(vd.getIntrinsicWidth(),
                    vd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        //return alpha bitmap of size 100x100
        return scaleBitmap(bitmap.extractAlpha(), bitmap.getWidth(), bitmap.getHeight(), 100f, 100f);
    }

    //get an alpha bitmap from a resource of size 100x100
    public static Bitmap getComparableBitmap(Drawable drawable) {
        //for storing result
        Bitmap bitmap;

        try {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        } catch (ClassCastException e) {
            VectorDrawable vd = (VectorDrawable) drawable;
            bitmap = Bitmap.createBitmap(vd.getIntrinsicWidth(),
                    vd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        //return alpha bitmap of size 100x100
        return scaleBitmap(bitmap.extractAlpha(), bitmap.getWidth(), bitmap.getHeight(), 100f, 100f);
    }

    //resize bitmap
    protected static Bitmap scaleBitmap(Bitmap cbOriginal, int width, int height, float newWidth, float newHeight) {
        //find scaling factors
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        //prepare transformation matrix
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight, width / 2f, height / 2f);
        //generate scaled bitmap
        return Bitmap.createBitmap(cbOriginal, 0, 0, width, height, matrix, true);
    }


    //get Direction from icons
    protected static void getDirection(Context context, @NonNull Intent intent) {

        Log.d(TAG, "getDirection: Inside PixelProcessingService");
        //return if Map is rerouting
        if (REROUTING.equals(intent.getStringExtra("type"))) {
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

        //get bitmap and get direction
        Bitmap currentBitmap = getComparableBitmap(d);
        int matchingRes = IconDataset.getMatchingIcon(currentBitmap);
        String direction = IconDataset.directionNames.get(matchingRes);
//        String filename = storeImage(currentBitmap,context);
//        Log.d(TAG, "Stored >> "+title+" | "+text+" | "+direction + " | "+filename);

        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;

        Log.d(TAG, "getDirection: DIRECTION DETECTED " + direction);
        Log.d(TAG, "getDirection: Processing time " + difference);

        //broadcast job done intent
        Intent jobDoneIntent = new Intent(DIRECTION_BROADCAST);
        jobDoneIntent.putExtra("type", DIRECTION_KNOWN);
        jobDoneIntent.putExtra("direction", direction);
        jobDoneIntent.putExtra("title", title);
        jobDoneIntent.putExtra("text", text);

        //broadcast the intent
        LocalBroadcastManager.getInstance(context).sendBroadcast(jobDoneIntent);
    }


    //Create a File for saving an image or video
    private static File getOutputMediaFile(Context context) {
        //adding random suffix to filename to avoid collision
        int random = (int) (Math.random() % 1000);
        random += System.nanoTime();
        random %= 1000;
        if (random < 0) random = Math.abs(random);

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
        String directoryPath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator;
        // Create a media file name
        String timeStamp = (new SimpleDateFormat("ddMMyy_HHmmssss").format(new Date()));
        File mediaFile;
        String mImageName = timeStamp + "_" + random + ".PNG";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
//        mediaFile = new File(directoryPath+ mImageName);
        return mediaFile;
    }

    //Write the bitmap to filesystem as a image
    public static String storeImage(Bitmap image, Context context) {
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return "File not created";
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

        return pictureFile.getPath();
    }


}
