package com.example.maptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String TAG = "TAGIT";
    public Bitmap og;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        /***********************  Generating original icon  ***************************/
        Drawable ogdrawable = appContext.getDrawable(R.drawable.right_image_edited);
        og = getBitmapFromDrawable(ogdrawable);
        og = PixelProcessingService.scaleBitmap(og.extractAlpha(),og.getWidth(),og.getHeight(),100f,100f);

        /***********************  Processing incoming icon  ***************************/
        Drawable drawable = ContextCompat.getDrawable(appContext,R.drawable.right_image);
        drawable.mutate();
        int width = 0,height = 0;
        Bitmap bitmap = getBitmapFromDrawable(drawable);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Log.d(TAG, "useAppContext: Bitmap Unprocessed >> "+width+","+height);
        Bitmap cb = PixelProcessingService.scaleBitmap(bitmap.extractAlpha(),width,height,100f,100f);
        width = cb.getWidth();
        height = cb.getHeight();
        Log.d(TAG, "useAppContext: Bitmap Processed >> "+width+","+height);

        Bitmap difference = cosineSimilarity(og,cb);
        String storedImage = PixelProcessingService.storeImage(difference, appContext);
        Log.d(TAG, "useAppContext: StoreDifference >> "+storedImage);

    }


    public Bitmap cosineSimilarity(Bitmap a, Bitmap b){
        Bitmap res = Bitmap.createBitmap(a);
        double sumAB=0,Asq=0,Bsq=0;

        for (int i = 0; i < a.getWidth(); i++) {
            for (int j = 0; j < a.getHeight(); j++) {
                int colorA = a.getPixel(i,j);
                int colorB = b.getPixel(i,j);

                int alphaA = Color.alpha(colorA);
                int alphaB = Color.alpha(colorB);

                int d = alphaA - alphaB;
                d = Math.abs(d);

                //for cosine diff
                sumAB+=(alphaA*alphaB);
                Asq+=(alphaA*alphaA);
                Bsq+=(alphaB*alphaB);

                res.setPixel(i,j,Color.argb(d,0,0,0));
            }
        }

        Asq = Math.sqrt(Asq);
        Bsq = Math.sqrt(Bsq);

        double similarity = (sumAB/(Asq*Bsq));
        Log.d(TAG, "diff: Similarity >> "+similarity);
        return res.extractAlpha();
    }

    public Bitmap getBitmapFromDrawable(Drawable drawable){
        Bitmap bitmap;
        try{
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }catch(ClassCastException e){
            VectorDrawable vd = (VectorDrawable) drawable;
            bitmap = Bitmap.createBitmap(vd.getIntrinsicWidth(),
                    vd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
