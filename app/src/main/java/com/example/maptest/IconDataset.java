package com.example.maptest;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

public class IconDataset {
    private static final String TAG = "IconDataset";
    public static HashMap<String, String> data = new HashMap<>();
    public static int datasetResources[] = null;
    protected static HashMap<Integer, ArrayList<Integer>> bitmapData = null;
    protected static HashMap<Integer, String> directionNames = null;

    private static void loadBitmapData(){
        if(datasetResources==null) loadDatasetResources();
        bitmapData = new HashMap<>();
        for(int res:datasetResources){
            Bitmap bitmap = PixelProcessingService.getComparableBitmap(App.context,res);
            ArrayList<Integer> alphaPixels = PixelProcessingService.getAlphaPixels(bitmap);
            bitmapData.put(res,alphaPixels);
        }
    }
    private static void loadDatasetResources() {
        if(datasetResources==null)
        datasetResources = new int[]{
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
        };
    }

    private static void mapDirectionsWithResources(){
        directionNames = new HashMap<>();
        directionNames.put(R.drawable.da_turn_arrive_right_svg,Directions.ARRIVED);
        directionNames.put(R.drawable.da_turn_arrive_svg,Directions.ARRIVED);
        directionNames.put(R.drawable.da_turn_depart_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_fork_right_svg,Directions.RIGHT);
        directionNames.put(R.drawable.da_turn_generic_roundabout_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_ramp_right_svg,Directions.RIGHT);
        directionNames.put(R.drawable.da_turn_right_svg,Directions.RIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_1_svg,Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_2_svg,Directions.RIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_3_svg,Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_4_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_5_svg,Directions.SLIGHT_LEFT);
        directionNames.put(R.drawable.da_turn_roundabout_6_svg,Directions.LEFT);
        directionNames.put(R.drawable.da_turn_roundabout_7_svg,Directions.SHARP_LEFT);
        directionNames.put(R.drawable.da_turn_roundabout_8_svg,Directions.U_LEFT);
        directionNames.put(R.drawable.da_turn_roundabout_exit_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_sharp_right_svg,Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.da_turn_slight_right_svg,Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.da_turn_straight_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_unknown_svg,Directions.UNKNOWN);
        directionNames.put(R.drawable.da_turn_uturn_svg,Directions.U_LEFT);
        directionNames.put(R.drawable.ic_alternate_route_svg,Directions.ALTERNATE);
        directionNames.put(R.drawable.ic_arrive_right_svg,Directions.ARRIVED);
        directionNames.put(R.drawable.ic_roundabout_exit_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.ic_roundabout_left_svg,Directions.LEFT);
        directionNames.put(R.drawable.ic_roundabout_right_svg,Directions.RIGHT);
        directionNames.put(R.drawable.ic_roundabout_sharp_left_svg,Directions.SHARP_LEFT);
        directionNames.put(R.drawable.ic_roundabout_sharp_right_svg,Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.ic_roundabout_slight_left_svg,Directions.SLIGHT_LEFT);
        directionNames.put(R.drawable.ic_roundabout_slight_right_svg,Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.ic_roundabout_straight_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.ic_roundabout_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.ic_roundabout_u_turn_svg,Directions.U_LEFT);
        directionNames.put(R.drawable.ic_straight_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.ic_turn_right_svg,Directions.RIGHT);
        directionNames.put(R.drawable.ic_turn_sharp_right_svg,Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.ic_turn_slight_right_svg,Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.ic_u_turn_svg,Directions.U_LEFT);
        directionNames.put(R.drawable.lane_normal_short_svg,Directions.RIGHT);
        directionNames.put(R.drawable.lane_normal_svg,Directions.RIGHT);
        directionNames.put(R.drawable.lane_sharp_short_svg,Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.lane_sharp_svg,Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.lane_slight_svg,Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.lane_slight_tall_svg,Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.lane_straight_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.lane_straight_tall_svg,Directions.STRAIGHT);
        directionNames.put(R.drawable.lane_uturn_short_svg,Directions.U_RIGHT);
        directionNames.put(R.drawable.lane_uturn_svg,Directions.U_RIGHT);

    }

    protected static boolean contains(Bitmap a){
        //load data if not loaded
        if (bitmapData==null)loadBitmapData();

        double maxValue=0;
        int maxId=0;
        ArrayList<Integer> target = PixelProcessingService.getAlphaPixels(a);

        for(HashMap.Entry res:bitmapData.entrySet()){
            double val = cosineSimilarity((ArrayList<Integer>) res.getValue(),target);
            if(val > maxValue){
                maxValue = val;
                maxId = (int) res.getKey();
            }
        }

        if(maxValue > 0.6)return true;

        return false;
    }

    private static double cosineSimilarity(ArrayList<Integer> a,ArrayList<Integer> b){
        int size = a.size();
        double similarity = 0;
        double sumAB=0,Asq=0,Bsq=0;
        long start_time = System.nanoTime();
        for (int i = 0; i < size; i++) {
            //extract alpha value from pixel value
            int alphaA = a.get(i);
            int alphaB = b.get(i);

            //calculate and update cosine similarity factors
            sumAB+=(alphaA*alphaB);
            Asq+=(alphaA*alphaA);
            Bsq+=(alphaB*alphaB);

        }
        //square root for denominator
        Asq = Math.sqrt(Asq);
        Bsq = Math.sqrt(Bsq);
        //calculate the similarity value
        similarity = (sumAB/(Asq*Bsq));

        return similarity;
    }

    protected static class Directions{
        public static final String ARRIVED = "ARR";

        public static final String STRAIGHT = "SS";

        public static final String SLIGHT_LEFT = "L";
        public static final String LEFT = "LL";
        public static final String SHARP_LEFT = "LLL";
        public static final String U_LEFT = "LLLL";

        public static final String SLIGHT_RIGHT = "R";
        public static final String RIGHT = "RR";
        public static final String SHARP_RIGHT = "RRR";
        public static final String U_RIGHT = "RRRR";

        public static final String UNKNOWN = "?";
        public static final String ALTERNATE = "LR";
    }

}