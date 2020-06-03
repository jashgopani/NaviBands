package com.example.maptest;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class CSVUtilities {
    private static final String TAG = "CSVUtilities";
    static CSVWriter csvWriter;
    static CSVReader csvReader;

    public static CSVWriter getCsvWriter() {
        return csvWriter;
    }

    public static CSVReader getCsvReader() {
        return csvReader;
    }

    public static boolean openCSVFileWriter(Context context, String path, String csvFilename, boolean append) {
        //open the filestream to write
        try {
            File file = new File(path);
            boolean created = file.mkdir();//create folder if doesnt exist
            //open filestream
            csvWriter = new CSVWriter(new FileWriter(csvFilename, append));
            Log.d(TAG, "openCSVFileWriter: Stream Opened successfully");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean closeCSVFileWriter(Context context) {
        if (csvWriter == null) {
            Log.d(TAG, "closeCSVFileWriter: File was never opened");
            return true;
        }
        try {
            csvWriter.close();
            Log.d(TAG, "closeCSVFileWriter: Stream Closed successfully");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeToCSVFile(String[] data) {

        try {
            csvWriter.writeNext(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
