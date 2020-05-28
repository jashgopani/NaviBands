package com.example.maptest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button startServiceBtn, stopServiceBtn, compareIconsBtn;
    ImageView imageIcon;
    TextView statustv, logtv;
    HashSet<String> largeIcons;
    HashMap<PixelWrapper, Bitmap> bitmapHashMap;
    Context context;
    File file;
    String path, csvFilename;

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            // To recieve intents from service, perform the actions of onRecieve method
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            String old = logtv.getText().toString();


            String not = "\n{title : " + title + "\ntext :" + text + " }\n";
            int nextTurnAfter = getTurnDistance(title);
            if (nextTurnAfter > -1 && nextTurnAfter < 20)
                System.out.println("next turn after : " + nextTurnAfter);


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
                String result = IconMap.icons.get(p.compressed);
                if (result != null) {
                    not += "Take " + result + " " + ((nextTurnAfter > -1 && nextTurnAfter <= 20) ? nextTurnAfter : "") + "\n\n̥";
                    Toast.makeText(context, "Match found  : " + result, Toast.LENGTH_SHORT).show();
                    imageIcon.setImageDrawable(icon);
                } else if (largeIcons.add(p.compressed)) {//if new icon detected
                    //display the icon
                    imageIcon.setImageDrawable(icon);

                    //for debugging
                    System.out.println("Bitmap is unique");

                    //Unregister reciever for the time being
                    unregisterBroadcastReceiver();

                    //save image as PNG for backup
                    storeImage(cb);

                    //show dialog and ask for label name
                    showAlertDialog(p.compressed, icon);


                } else {
                    System.out.println("Bitmap already exists");
                }

                System.out.println("HashSet Size : " + largeIcons.size());
                System.out.println("--------------------------Pixels End--------------------------------");

            }
            //update the text on screen
            logtv.setText(not + old);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getPermissions();
    }

    private void init() {
        context = getApplicationContext();

        path = context.getExternalFilesDir(null) + File.separator;
        csvFilename = path + "PixelData.csv";

        startServiceBtn = findViewById(R.id.startServiceBtn);
        stopServiceBtn = findViewById(R.id.stopServiceBtn);
        compareIconsBtn = findViewById(R.id.saveIconBtn);
        imageIcon = findViewById(R.id.imageIcon);
        statustv = findViewById(R.id.statustv);
        logtv = findViewById(R.id.logtv);
        largeIcons = new HashSet<>();
        bitmapHashMap = new HashMap<>();


        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open file first
                if (CSVUtilities.openCSVFileWriter(context, path, csvFilename, true))
                    registerBroadcastReceiver();//register broadcast recieveer
                else
                    Log.i(TAG, "File did not open");

                Intent intent = new Intent(context,ForegroundService.class);
                intent.putExtra("title","NaviBands");
                ContextCompat.startForegroundService(context,intent);
                statustv.setText("Monitoring onn");
            }
        });

        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close file first
                if (CSVUtilities.closeCSVFileWriter(context))
                    unregisterBroadcastReceiver();//register broadcast recieveer
                else
                    Log.i(TAG, "File did not close");

                stopService(new Intent(context,ForegroundService.class));
                statustv.setText("Monitoring off");
//                stopService(new Intent(context,TempService.class));
            }
        });

        compareIconsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = false;
                if (CSVUtilities.getCsvWriter() != null) {
                    result = CSVUtilities.writeToCSVFile(new String[]{"key", "Value"});
                } else {
                    result = CSVUtilities.openCSVFileWriter(context, path, csvFilename, true);
                }
                String msg = result ? "Test Successsful" : "Test Failed";
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        CSVUtilities.openCSVFileWriter(context, path, csvFilename, true);
        CSVUtilities.writeToCSVFile(new String[]{"onDestroy", new Date().toString()});
        for (Map.Entry element : IconMap.icons.entrySet()) {
            CSVUtilities.writeToCSVFile(new String[]{element.getKey().toString(), element.getValue().toString()});
        }
        CSVUtilities.closeCSVFileWriter(context);
        super.onDestroy();
    }

    private void getPermissions() {
        //Check for Notification access
        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            //service is enabled do something
            Log.i(TAG, "Noti access enabled");
        } else {
            //service is not enabled try to enabled by calling...
            startActivity(new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }

        //check storage permissions & ask for it if not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        //check location permissions & ask for it if not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(onNotice, new IntentFilter("Msg"));
        statustv.setText("Monitoring ON");
        Toast.makeText(context, "Monitoring ON", Toast.LENGTH_SHORT).show();
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(onNotice);
        statustv.setText("Monitoring OFF");
        Toast.makeText(context, "Monitoring OFF", Toast.LENGTH_SHORT).show();
    }

    private int getTurnDistance(String a) {

        String pattern = "^\\d+";
        Matcher m = (Pattern.compile(pattern)).matcher(a);
        String res = m.find() ? m.group().trim() : "-1";
        System.out.println("Your REGEX answer : " + Integer.parseInt(res));
        return Integer.parseInt(res);
    }

    private void showAlertDialog(String compressedValue, Drawable icon) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("New Icon");
        alertDialog.setMessage("Enter Icon Name");
        alertDialog.setCancelable(false);

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(icon);

        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String iconName = input.getText().toString().trim().toUpperCase();
                        iconName = iconName.length() > 0 ? iconName : "unknown";
                        if (iconName.length() > 0) {
                            //write to file
                            IconMap.icons.put(compressedValue, iconName);
                            CSVUtilities.writeToCSVFile(new String[]{compressedValue, iconName});

                            //Register reciever again
                            registerBroadcastReceiver();
                        } else {

                            Toast.makeText(MainActivity.this, "Enter valid name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.show();
    }


    //Create a File for saving an image or video
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
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    //Write the bitmap to filesystem as a image
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
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
}

/* {{...}} part of code will be executed on other thread
 * app start                                         --notif listener starts
 * >>assume that collection of pixels exist as csv
 * >>load that into hashmap using opencsv
 * start service --reciver register
 *   if(got notif)
 *       get title,text,icon object
 *       icon -> bitmap drawable
 *       bitmap drawable -> bitmap
 *       {{ bitmap -> turn binary -> bitmap
 *       bitmap -> extract pixels
 *       compress pixels }}
 *       if(this compressed version exists) then ignore
 * >>      else (this part is only for developer)
 *           show alert
 *               save to excel file
 * stop service --unregister reciver
 * */