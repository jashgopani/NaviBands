package com.example.maptest;


public class Constants {
    public static final String PACKAGE = "com.example.maptest";
    public static final String MAPS_PACKAGE = "com.google.android.apps.maps";
    public static final String NOTIFICATION_RECEIVED = "originNotificationListenerService";
    public static final String JOB_DONE = "originPixelProcessingService";
    public static final String ENCODED_DATA = "compressedPixelString";
    public static final String DIRECTION = "directionName";
    public static final String ICON_NULL = "notificationLargeIconNull";
    public static final String REROUTING = "Rerouting...";
    public static final String PROCESSED_NOTIFICATION = "icon detected direction";
    public static final String DIRECTION_BROADCAST = "icon detected direction";
    public static final String CSV_FILENAME = "PixelData.csv";
    public static final String DIRECTION_UNKNOWN = "Direction 404";
    public static final String DIRECTION_KNOWN = "Direction Found";
    public static String DEBUG_PATH = "";

}


//Temp MainActivity full file
//
//public class MainActivity extends AppCompatActivity {
//    private static final String TAG = "MainActivity";
//    Button startServiceBtn, stopServiceBtn, compareIconsBtn;
//    ToggleButton devModeBtn;
//    ImageView imageIcon;
//    TextView statustv, logtv;
//    HashSet<String> largeIcons;
//    HashMap<PixelWrapper, Bitmap> bitmapHashMap;
//    Context context;
//    File file;
//    String path, csvFilename;
//    boolean devMode=false;
//
////    private BroadcastReceiver onNotice = new BroadcastReceiver() {
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            if(devMode)detectAndSaveIcons(context,intent);
////            else updateUI(context,intent);
////        }
////    };
////
////    private BroadcastReceiver onJobDone = new BroadcastReceiver() {
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            Icon ic = intent.getParcelableExtra("icon");
////            Drawable d = ic.loadDrawable(context);
////            BitmapDrawable cb = (BitmapDrawable)d;
////            Bitmap bitmap = cb.getBitmap();
////
////            String compressed = intent.getStringExtra(PIXEL_DATA);
////            if(Dataset.icons.containsKey(compressed)){
////                //means this is new icon
////                showAlertDialog(compressed,ic.loadDrawable(context));
////                storeImage(bitmap);
////                registerBroadcastReceiver();
////            }else{
////                //already exists
////                logtv.setText(logtv.getText().toString().concat("\nDirection "+intent.getStringExtra(DIRECTION)));
////            }
////        }
////    };
////
////    private void updateUI(Context context, Intent intent) {
////    }
////
////    private void detectAndSaveIcons(Context context, Intent intent){
////        if (!REROUTING.equals(intent.getStringExtra("type"))) {
////            Icon ic = intent.getParcelableExtra("icon");
////            imageIcon.setImageDrawable(ic.loadDrawable(context));
////            PixelProcessingService.enqueueWork(context,intent);
////            LocalBroadcastManager.getInstance(context).unregisterReceiver(onNotice);
////        }
////    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        init();
//        getPermissions();
//    }
//
//    private void init() {
//        context = getApplicationContext();
//
//        path = context.getExternalFilesDir(null) + File.separator;
//        csvFilename = path + "PixelData.csv";
//
//        startServiceBtn = findViewById(R.id.startServiceBtn);
//        stopServiceBtn = findViewById(R.id.stopServiceBtn);
//        compareIconsBtn = findViewById(R.id.saveIconBtn);
//        devModeBtn = findViewById(R.id.devModeBtn);
//        imageIcon = findViewById(R.id.imageIcon);
//        statustv = findViewById(R.id.statustv);
//        logtv = findViewById(R.id.logtv);
//        largeIcons = new HashSet<>();
//        bitmapHashMap = new HashMap<>();
//
//
//        startServiceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //open file first
////                if (CSVUtilities.openCSVFileWriter(context, path, csvFilename, true))
////                    registerBroadcastReceiver();//register broadcast recieveer
////                else
////                    Log.i(TAG, "File did not open");
//
//                Intent intent = new Intent(context,ForegroundService.class);
//                intent.putExtra("title","NaviBands");
//                intent.putExtra("text","Navigation Started");
//                ContextCompat.startForegroundService(context,intent);
//                Log.d(TAG, "onClick: Started Foreground Service");
//                statustv.setText("Monitoring onn");
//            }
//        });
//
//        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //close file first
////                if (CSVUtilities.closeCSVFileWriter(context))
////                    unregisterBroadcastReceiver();//register broadcast recieveer
////                else
////                    Log.i(TAG, "File did not close");
//
//                stopService(new Intent(context,ForegroundService.class));
//                statustv.setText("Monitoring off");
////                stopService(new Intent(context,TempService.class));
//            }
//        });
//
//        compareIconsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                boolean result = false;
////                if (CSVUtilities.getCsvWriter() != null) {
////                    result = CSVUtilities.writeToCSVFile(new String[]{"key", "Value"});
////                } else {
////                    result = CSVUtilities.openCSVFileWriter(context, path, csvFilename, true);
////                }
////                String msg = result ? "Test Successsful" : "Test Failed";
////                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        devModeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                devMode=isChecked;
//                if(isChecked){
//                    buttonView.setTextColor(Color.parseColor("#ff0000"));
//                }else{
//                    buttonView.setTextColor(Color.parseColor("#000000"));
//                }
//                Log.d(TAG, "onCheckedChanged: New state >>"+(devMode?"DEV ON":"DEV OFF"));
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
////        CSVUtilities.openCSVFileWriter(context, path, csvFilename, true);
////        CSVUtilities.writeToCSVFile(new String[]{"onDestroy", new Date().toString()});
////        for (Map.Entry element : Dataset.icons.entrySet()) {
////            CSVUtilities.writeToCSVFile(new String[]{element.getKey().toString(), element.getValue().toString()});
////        }
////        CSVUtilities.closeCSVFileWriter(context);
//        super.onDestroy();
//    }
//
//    private void getPermissions() {
//        //Check for Notification access
//        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
//            //service is enabled do something
//            Log.i(TAG, "Noti access enabled");
//        } else {
//            //service is not enabled try to enabled by calling...
//            startActivity(new Intent(
//                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//        }
//
//        //check storage permissions & ask for it if not granted
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//        }
//        //check location permissions & ask for it if not granted
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
//        }
//    }

//    private void registerBroadcastReceiver() {
//        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(onNotice, new IntentFilter(NOTIFICATION_RECEIVED));
//        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(onJobDone, new IntentFilter(JOB_DONE));
//        statustv.setText("Monitoring ON");
//        Toast.makeText(context, "Monitoring ON", Toast.LENGTH_SHORT).show();
//    }
//
//    private void unregisterBroadcastReceiver() {
//        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(onNotice);
//        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(onJobDone);
//        statustv.setText("Monitoring OFF");
//        Toast.makeText(context, "Monitoring OFF", Toast.LENGTH_SHORT).show();
//    }
//
//    private int getTurnDistance(String a) {
//
//        String pattern = "^\\d+";
//        Matcher m = (Pattern.compile(pattern)).matcher(a);
//        String res = m.find() ? m.group().trim() : "-1";
//        System.out.println("Your REGEX answer : " + Integer.parseInt(res));
//        return Integer.parseInt(res);
//    }
//
//    private void showAlertDialog(String compressedValue, Drawable icon) {
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
//        alertDialog.setTitle("New Icon");
//        alertDialog.setMessage("Enter Icon Name");
//        alertDialog.setCancelable(false);
//
//        final EditText input = new EditText(MainActivity.this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        input.setLayoutParams(lp);
//        alertDialog.setView(input);
//        alertDialog.setIcon(icon);
//
//        alertDialog.setPositiveButton("Save",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        String iconName = input.getText().toString().trim().toUpperCase();
//                        iconName = iconName.length() > 0 ? iconName : "unknown";
//                        if (iconName.length() > 0) {
//                            //write to file
//                            Dataset.icons.put(compressedValue, iconName);
//                            CSVUtilities.writeToCSVFile(new String[]{compressedValue, iconName});
//
//                            //Register reciever again
//                            registerBroadcastReceiver();
//                        } else {
//
//                            Toast.makeText(MainActivity.this, "Enter valid name", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//        alertDialog.show();
//    }
//
//
//    //Create a File for saving an image or video
//    private File getOutputMediaFile() {
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + getApplicationContext().getPackageName()
//                + "/Files");
//
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                return null;
//            }
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
//        File mediaFile;
//        String mImageName = "MI_" + timeStamp + ".jpg";
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
//        return mediaFile;
//    }
//
//    //Write the bitmap to filesystem as a image
//    private void storeImage(Bitmap image) {
//        File pictureFile = getOutputMediaFile();
//        if (pictureFile == null) {
//            Log.d(TAG,
//                    "Error creating media file, check storage permissions: ");// e.getMessage());
//            return;
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.close();
//            Log.i(TAG, "Image saved successfully");
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "Error accessing file: " + e.getMessage());
//        }
//    }
//
//    //remove rgb channels from bitmap
//    public Bitmap turnBinary(Bitmap origin) {
//        int width = origin.getWidth();
//        int height = origin.getHeight();
//        Bitmap bitmap = origin.copy(Bitmap.Config.ARGB_8888, true);
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                int col = bitmap.getPixel(i, j);
//                int alpha = (col & 0xFF000000) >> 24;
//                int red = (col & 0x00FF0000);
//                int green = (col & 0x0000FF00) >> 8;
//                int blue = (col & 0x000000FF) >> 16;
//                int newColor = alpha | blue | green | red;
//                bitmap.setPixel(i, j, newColor);
//            }
//        }
//        return bitmap;
//    }
//}

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