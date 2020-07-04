package com.example.maptest;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jashgopani.github.io.mibandsdk.models.CustomVibration;

import static org.apache.commons.lang3.StringUtils.difference;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    /*
    700 m - Devidas Rd , 0 m - Kalpana Chawla Marg ,
    * */

    @Test
    public void titleTest(){
        String title = "70 m - Devidas Rd";
        String t = title.substring(0,title.indexOf("-")).trim();
        char[] c = t.toCharArray();
        if(!(c[t.length()-2]=='k'))System.out.println(Integer.parseInt(t.substring(0,t.length()-2)));
        //get distance
        String pattern = "^\\d+";
        Matcher m = (Pattern.compile(pattern)).matcher(t);
        String res = m.find() ? m.group().trim() : "-1";
        System.out.println("Your REGEX answer : " + Integer.parseInt(res));
    }

    @Test
    public void detectType(){
        Integer[] integers = CustomVibration.generatePattern("600", ",");
        System.out.println(Arrays.toString(integers));
        String direction = "U";
        assertTrue(direction.endsWith("U"));
    }
}