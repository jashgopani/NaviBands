package com.example.maptest;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BitmapWrapper implements Serializable {
    Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public BitmapWrapper(Bitmap image) {
        this.image = image;
    }

    public static void saveBitmapWrapper(Context context, String fileName, BitmapWrapper object) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(object);
        os.close();
        fos.close();
    }

    public static void loadBitmapWrapper(Context context, String fileName, BitmapWrapper object) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream is = new ObjectInputStream(fis);
        object = (BitmapWrapper) is.readObject();
        is.close();
        fis.close();
    }


}
