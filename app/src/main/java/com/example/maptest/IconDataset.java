package com.example.maptest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

        mapDirectionsWithResources();
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
        directionNames.put(-1,Directions.UNKNOWN);

        //sort by value so that same directions remain together
        directionNames = sortDirectionNamesByValue(directionNames);
    }

    public static HashMap<Integer, String> sortDirectionNamesByValue(HashMap<Integer, String> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, String> > list =
                new LinkedList<Map.Entry<Integer, String> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, String> >() {
            public int compare(Map.Entry<Integer, String> o1,
                               Map.Entry<Integer, String> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Integer, String> temp = new LinkedHashMap<Integer, String>();
        for (Map.Entry<Integer, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    protected static int getMatchingIcon(Bitmap a){

        //load data if not loaded
        if (bitmapData==null)loadBitmapData();

        double maxSimilarity=0,prevSimilarity=0;
        int maxId=0;
        String prevDirection = "";
        ArrayList<Integer> targetPixels = PixelProcessingService.getAlphaPixels(a);

        //traverse array and compare
        for(Map.Entry<Integer, ArrayList<Integer>> res:bitmapData.entrySet()){

            //get current direction name from current resId
            int currResId = res.getKey();
            String currDirection = directionNames.containsKey(currResId)?directionNames.get(currResId):Directions.UNKNOWN;
            ArrayList<Integer> currPixels = res.getValue();

            //calc similarity
            double val = cosineSimilarity(currPixels,targetPixels);

            Log.d(TAG, "comparing with: "+currDirection+" | "+val);

            if(val > maxSimilarity){
                maxSimilarity = val;
                maxId = currResId;//for debugging

                double diff = Math.abs(val-prevSimilarity);
                if(diff>=0 && diff<=0.15d && currDirection!=null && currDirection.equals(prevDirection) && maxSimilarity>0.6){
                    //if the previous similarity is almost same and the direction is also same
                    //no redundant comparison needed
                    Log.d(TAG, "contains: DirectionDetected >> "+currDirection);
                    return maxId;
                }

            }
            //store prev direction name and prev
            prevSimilarity = val;
            prevDirection = directionNames.get(currResId);
        }

        return maxId;
    }

    private static double cosineSimilarity(ArrayList<Integer> a,ArrayList<Integer> b){
        int size = a.size();
        double similarity = 0;
        double sumAB=0,Asq=0,Bsq=0;

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
        public static final String ARRIVED = "ARRIVED";

        public static final String STRAIGHT = "STRAIGHT";

        public static final String SLIGHT_LEFT = "SLIGHT LEFT";
        public static final String LEFT = "LEFT";
        public static final String SHARP_LEFT = "SHARP LEFT";
        public static final String U_LEFT = "U-LEFT";

        public static final String SLIGHT_RIGHT = "SLIGHT RIGHT";
        public static final String RIGHT = "RIGHT";
        public static final String SHARP_RIGHT = "SHARP RIGHT";
        public static final String U_RIGHT = "U-RIGHT";

        public static final String UNKNOWN = "UNKNOWN";
        public static final String ALTERNATE = "LR";
    }

}