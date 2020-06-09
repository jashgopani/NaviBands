package com.example.maptest;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.maptest.Constants.CSV_FILENAME;
import static com.example.maptest.Constants.DEBUG_PATH;
import static com.example.maptest.MainActivity.PATH;

public class IconDataset {
    public static Context context;
    private static final String TAG = "IconDataset";
    public static HashMap<String, String> data = new HashMap<>();
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void setContext(Context context) {
        IconDataset.context = context;
    }

    static void ff(){
        //load data into dataset here
        db.collection("dataset").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot q: task.getResult()){
                        Map<String, Object> document = q.getData();
                    }
                }
            }
        });
    }

    static {
//        readCSVFiles();
    }

    public static void readCSVFiles(){
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(DEBUG_PATH+CSV_FILENAME));
            while ((sCurrentLine = br.readLine()) != null) {
//                System.out.println();
                Log.d(TAG, "readCSVFiles: "+sCurrentLine.substring(0,15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}