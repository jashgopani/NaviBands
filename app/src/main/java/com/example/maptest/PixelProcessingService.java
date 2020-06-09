package com.example.maptest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.math.MathUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.DIRECTION_KNOWN;
import static com.example.maptest.Constants.DIRECTION_UNKNOWN;
import static com.example.maptest.Constants.ICON_NULL;
import static com.example.maptest.Constants.ENCODED_DATA;
import static com.example.maptest.Constants.REROUTING;
import static com.example.maptest.MainActivity.history;

public class PixelProcessingService {
    private static final String TAG = "PixelProcessingService";

    //remove rgb channels from bitmap
    protected static Bitmap turnBinary(Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        Bitmap bitmap = origin.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int col = bitmap.getPixel(i, j);
                int alpha = ((col & 0xFF000000) >> 24);
                int red = (col & 0x00FF0000);
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF) >> 16;
                int newColor = alpha | blue | green | red;
                bitmap.setPixel(i, j, newColor);
            }
        }
        return bitmap;
    }

    //resize bitmap
    protected static Bitmap scaleBitmap(Bitmap cbOriginal, int width, int height, float newWidth, float newHeight){
        //find scaling factors
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        //prepare transformation matrix
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight, width / 2f, height / 2f);
        //generate scaled bitmap
        return Bitmap.createBitmap(cbOriginal, 0, 0, width, height, matrix, true);
    }

    //get base64 encoded String
    protected static String getEncodedBitmap(Bitmap cb){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        cb.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
        Bitmap cbOriginal = icon.getBitmap().extractAlpha();//remove color channels for faster calc
        int width = cbOriginal.getWidth();
        int height = cbOriginal.getHeight();

        Bitmap cb = scaleBitmap(cbOriginal,width,height,100f,100f);

        width = cb.getWidth();
        height = cb.getHeight();

        Log.d(TAG, "getDirection: " + width + "x" + height);

        //get base64 string
        String encodedString = getEncodedBitmap(cb);

        //get the direction name
        String direction = detectDirection(encodedString);

        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;

        Log.d(TAG, "getDirection: DIRECTION DETECTED " + direction);
        Log.d(TAG, "getDirection: Processing time " + difference);

        String imageName = null;
        if(!history.containsKey(encodedString)){
            imageName = storeImage(cb, context);
            history.put(encodedString,imageName==null?"FILENAME":imageName);
        }

        //broadcast job done intent
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
        jobDoneIntent.putExtra(ENCODED_DATA, encodedString);
        jobDoneIntent.putExtra("filename",imageName);
        //broadcast the intent
        LocalBroadcastManager.getInstance(context).sendBroadcast(jobDoneIntent);
    }

    private static String detectDirection(String encodedIcon) {
        for (Map.Entry element : IconDataset.data.entrySet()) {
            String pattern = (String) element.getKey();
            if(StringUtils.contains(encodedIcon,pattern))return (String) element.getValue();
        }
        return null;
    }

    //Create a File for saving an image or video
    private static File getOutputMediaFile(Context context) {
        //adding random suffix to filename to avoid collision
        int random = (int) (Math.random()%1000);
        random+=System.nanoTime();
        random%=1000;
        if(random<0)random=Math.abs(random);

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
        String mImageName = timeStamp+"_"+random+ ".PNG";
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
