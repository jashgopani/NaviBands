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

import org.junit.Assert;
import org.junit.Before;
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
    Context appContext;

    /*  Approach
    * Get drawables and convert them to bitmap of size 100x100
    * calculate cosine similarity and give result
    * */
    @Before
    public void init(){
        // Context of the app under test.
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    public boolean areSimilar(int resA,int resB){
        Bitmap a = getComparableBitmap(resA);
        Assert.assertNotNull(a);
        Bitmap b = getComparableBitmap(resB);
        Assert.assertNotNull(a);
        boolean isSimilar = cosineSimilarity(appContext,a,b,0,0,a.getWidth(),a.getHeight());
        return isSimilar;
    }

    public boolean areSimilar(int resA,int resB,int xmin,int ymin,int xmax,int ymax){
        Bitmap a = getComparableBitmap(resA);
        Assert.assertNotNull(a);
        Bitmap b = getComparableBitmap(resB);
        Assert.assertNotNull(a);
        boolean isSimilar = cosineSimilarity(appContext,a,b,xmin,ymin,xmax,ymax);
        return isSimilar;
    }

    public boolean cosineSimilarity(Context c,Bitmap a, Bitmap b,int xmin,int ymin,int xmax,int ymax){
        int xMin = xmin;
        int yMin = ymin;
        int xMax = xmax;
        int yMax = ymax;
        double threshold = 0.6d;
        Bitmap res = Bitmap.createBitmap(a);
        double sumAB=0,Asq=0,Bsq=0;
        long start_time = System.nanoTime();
        for (int i = xMin; i < xMax; i++) {
            for (int j = yMin; j < yMax; j++) {
                //get the pixel values
                int colorA = a.getPixel(i,j);
                int colorB = b.getPixel(i,j);
                //extract alpha value from pixel value
                int alphaA = Color.alpha(colorA);
                int alphaB = Color.alpha(colorB);
                //find the absolute difference in alpha values  for storing result image
                int d = Math.abs(alphaA - alphaB);
                //calculate and update cosine similarity factors
                sumAB+=(alphaA*alphaB);
                Asq+=(alphaA*alphaA);
                Bsq+=(alphaB*alphaB);
                //set the pixel value based on alpha values only
                res.setPixel(i,j,Color.argb(d,0,0,0));
            }
        }
        //square root for denominator
        Asq = Math.sqrt(Asq);
        Bsq = Math.sqrt(Bsq);
        //calculate the similarity value
        double similarity = (sumAB/(Asq*Bsq));

        //debugging part
        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;
        Log.d(TAG, "cosineSimilarity: Comparison Time >> "+difference+"ms");
        Log.d(TAG, "cosineSimilarity: Similarity >> "+similarity);
        Log.d(TAG, "results >> "+(similarity>threshold?"Same Icons":"Different Icons"));
        String storedImage = PixelProcessingService.storeImage(res.extractAlpha(), c);
        Log.d(TAG, "cosineSimilarity: StoreDifference >> "+storedImage);

        return (similarity > threshold);
    }

    public Bitmap getComparableBitmap(int resId){
        //for storing result
        Bitmap bitmap;

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        //get drawable from resource id
        Drawable drawable = ContextCompat.getDrawable(appContext,resId);
        drawable = drawable.mutate();

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

        //return alpha bitmap of size 100x100
        return PixelProcessingService.scaleBitmap(bitmap.extractAlpha(),bitmap.getWidth(),bitmap.getHeight(),100f,100f);
    }

    @Test
    public void TestIcons(){
        /*
        R.drawable.da_turn_arrive_right_svg,
R.drawable.da_turn_arrive_svg,
R.drawable.da_turn_depart_svg,
R.drawable.da_turn_ferry_svg,
R.drawable.da_turn_fork_right_svg,
R.drawable.da_turn_generic_merge_svg,
R.drawable.da_turn_generic_roundabout_svg,
R.drawable.da_turn_ramp_right_svg,
R.drawable.da_turn_right_svg,
R.drawable.da_turn_roundabout_1_svg,
R.drawable.da_turn_roundabout_2_svg,
R.drawable.da_turn_roundabout_3_svg,
R.drawable.da_turn_roundabout_4_svg,
R.drawable.da_turn_roundabout_5_svg,
R.drawable.da_turn_roundabout_6_svg,
R.drawable.da_turn_roundabout_7_svg,
R.drawable.da_turn_roundabout_8_svg,
R.drawable.da_turn_roundabout_exit_svg,
R.drawable.da_turn_sharp_right_svg,
R.drawable.da_turn_slight_right_svg,
R.drawable.da_turn_straight_svg,
R.drawable.da_turn_unknown_svg,
R.drawable.da_turn_uturn_svg,
R.drawable.ic_alternate_route_svg,
R.drawable.ic_arrive_right_svg,
R.drawable.ic_roundabout_exit_svg,
R.drawable.ic_roundabout_left_svg,
R.drawable.ic_roundabout_right_svg,
R.drawable.ic_roundabout_sharp_left_svg,
R.drawable.ic_roundabout_sharp_right_svg,
R.drawable.ic_roundabout_slight_left_svg,
R.drawable.ic_roundabout_slight_right_svg,
R.drawable.ic_roundabout_straight_svg,
R.drawable.ic_roundabout_svg,
R.drawable.ic_roundabout_u_turn_svg,
R.drawable.ic_straight_svg,
R.drawable.ic_turn_right_svg,
R.drawable.ic_turn_sharp_right_svg,
R.drawable.ic_turn_slight_right_svg,
R.drawable.ic_u_turn_svg,
R.drawable.lane_normal_short_svg,
R.drawable.lane_normal_svg,
R.drawable.lane_sharp_short_svg,
R.drawable.lane_sharp_svg,
R.drawable.lane_slight_svg,
R.drawable.lane_slight_tall_svg,
R.drawable.lane_straight_svg,
R.drawable.lane_straight_tall_svg,
R.drawable.lane_stub_svg,
R.drawable.lane_uturn_short_svg,
R.drawable.lane_uturn_svg,

        * */
    }

    @Test
    public void Sandbox(){
        Assert.assertFalse(areSimilar(R.drawable.ic_roundabout_exit_svg,R.drawable.ic_launcher_foreground));
    }

}
