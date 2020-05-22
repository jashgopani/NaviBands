package com.example.maptest;

import java.util.Arrays;

import androidx.annotation.NonNull;

public class PixelWrapper {
    Integer[] pixels;


    PixelWrapper(Integer[] p) {
        this.pixels = p.clone();
    }

    public Integer[] getPixels() {
        return pixels;
    }

    public void setPixels(Integer[] pixels) {
        this.pixels = pixels;
    }

    public boolean isEqualTo(PixelWrapper obj) {
        Integer[] other = obj.getPixels();
        Integer[] own= getPixels();
        for (int i=0;i<own.length;i++){
            if(!own[i].equals(other[i])){
                return false;
            }
        }
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return Arrays.toString(pixels);
    }


}
