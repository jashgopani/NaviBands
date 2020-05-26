package com.example.maptest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class PixelWrapper {
    public ArrayList<Integer> pixels;
    public int zeros;
    public String compressed;

    public PixelWrapper(ArrayList<Integer> pixels) {
        this.pixels = pixels;
        this.compressed = "";
    }

    public PixelWrapper() {
        this.compressed = "";
        pixels = new ArrayList<>();
    }

    public ArrayList<Integer> getPixels() {
        return pixels;
    }

    public void incrementZeros() {
        this.zeros += 1;
    }

    public String getCompressed() {
        return compressed;
    }

    public void setPixels(ArrayList<Integer> pixels) {
        this.pixels = pixels;
    }

    public int getZeros() {
        return zeros;
    }

    public void setZeros(int zeros) {
        this.zeros = zeros;
    }

    @NonNull
    @Override
    public String toString() {
        if (compressed.length() == 0) compressPixels();
        return compressed;
    }

    public boolean equals(@NonNull PixelWrapper other) {
        if (this.compressed.length() > 0 && other.compressed.length() > 0) {
            return this.compressed.equals(other.compressed);
        }
        System.out.println("this object is not compressed");
        return false;
    }

    public boolean isZero() {
        return getZeros() == getPixels().size();
    }

    public void printPixels() {
        for (int i : getPixels()) {
            System.out.print(i);
        }
    }

    public void compressPixels() {
        if (isZero()) {
            compressed = "(0){" + pixels.size() + "}";
        } else {

            StringBuilder rleString = new StringBuilder();
            int rleCounter = 1;

            for (int i = 0; i < pixels.size(); i++) {
                if (i > 0) {
                    int curr = pixels.get(i);
                    int prev = pixels.get(i - 1);
                    if (curr == prev) {
                        rleCounter++;
                    } else {
                        if (rleCounter > 1) {//if the value occurs more than once only then append the count
                            rleString.append(prev);
                            rleString.append("{" + rleCounter + "}");
                        } else {//if the value occured only once dont append count
                            rleString.append("("+prev+")");
                        }
                        rleCounter = 1;
                    }
                }
            }
            compressed = rleString.toString();
        }
    }

    public void printPixels(int w, int h) {

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                System.out.print(pixels.get(i * w + j));
                System.out.print(",");
            }
            System.out.println(">>\n");
        }
    }
}
