package com.example.maptest;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
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
    static CSVWriter csvWriter;
    static CSVReader csvReader;

    public static CSVWriter getCsvWriter() {
        return csvWriter;
    }

    public static CSVReader getCsvReader() {
        return csvReader;
    }

    public static boolean openCSVFileWriter(Context context, String path, String csvFilename, boolean append){
        //open the filestream to write
        try {
            File file = new File(path);
            boolean created = file.mkdir();//create folder if doesnt exist
            //open filestream
            csvWriter = new CSVWriter(new FileWriter(csvFilename, append));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean closeCSVFileWriter(Context context) {
        try {
            csvWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeToCSVFile(String[] data){

        try{
            csvWriter.writeNext(data);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean readFromCSVFile(Context context, String path, String csvFilename){

//        try {
//
//            csvReader = new CSVReader();
//            String[] nextLine;
//            while ((nextLine = csvReader.readNext()) != null) {
//                // nextLine[] is an array of values from the line
//                System.out.println(nextLine[0] + nextLine[1] + "etc...");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "The specified file was not found", Toast.LENGTH_SHORT).show();
//        }
        return true;
    }
}
