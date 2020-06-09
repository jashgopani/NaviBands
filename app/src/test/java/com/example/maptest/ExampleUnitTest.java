package com.example.maptest;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.StringUtils.difference;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    String original, cropped,difference,other;
    @Before
    public void init(){
        original = "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAABHNCSVQICAgIfAhkiAAABE5JREFUeJzt3DuIHVUcx/HfX416EdQFwSJEiIXYKWglq2InCloYiMbCFxLRwsQQWBXMQ1AEjYUPdA0miCISIQipbART2AhqmoAWimIUfMVgElkxX4szF8Mmu3fm7Nxzzsz8P7DNJTPnf/LbmTmPuSs555xzzjnnnHPOOeec6z7LXQAwI+niRR9/b2bkqGfQgMuBdznTbO7aBge4Enj/LGGM3Za7xsEA1gDvLRMGwHHgrty19h6wGtg9IYyx34ANuWvureqZsadmGGNf5K47pfNSNQSMJM1LuiNVm12ULBBJ+yTdnrA9txTgk4a3qbGfgOty198rwFeRYfwJrM5df69UYZyKDGTx7N2tBPG3KYBLctffG8AIOBAZxDFgTe4+9AZhnvFRZBjfAFfl7kNvEGbgeyLDOAxck7sPvUFYm6q7HLLYIeCW3H3oDcKq7aSFwqV8BtyUuw+9QXhmLLeEPimMm3P3oTeAGc6+uVTHIb8yWkQY2u6LDOMw/sxYUtSeOnBQUuw261FJX0Yem5WZTf0XqXEgwKeSbpxCLV1w+i/S52b2cNsNNAqkujJukHRO24V00N+Sfpb0tpk929ZJawdC2Lm7tq2Ge2a7me1o40QTAyHs9H2s+GfGkDwqad7M/o09QZ1bz05J18c2MDCvS1oHrIo9Qa1bFvCWpHsljWIbGpj1kvab2T9ND6z1cK5GE29IOtG0gYH6QNL9QON3FmqPlszsCYVLcqFpIwM1L6nxsDhmHvKcpCebHjdUZtbo/zh2pv68pLmYYyV9J2lv5LGp3S3p6hWeY87MXqj7j2Pfy3pG0q+SXow49kJJv5vZK5FtJ1OtSlxx2kd7I06zE7jMzLa2U9USqgXGTZELjEeAB6da4BQAdwIbIvr7baoCLwKejgzlB+C+JIW2CDgXeKhhX08Spg5JChwBWyJD+QN4IEmhLQJWAesb9jXdS+NVgXORoZwA7klWbEsIV0qT21f6t/iBbZGhQAe3cwnPlHIDqYp8DViIDGUWyP5F1CaAxxv0b/+k87W+r2Fmj0l6VdJfEYcflHQr0KX9ll8U9kXKBrxMGF3E6NT3SKh/q05/hYyZ2WZJ70hqvOIp6QAD/RbuVG8NZrZRcbN5SfqQDg6JV2rq92oze0rS9ohDR5J2ARvbrahsSR6e1X5zzArxpZIeabmcoqUczbyksCjplpEskGo7c5ekTana7KKk430zOy5pt6TNNQ85orA/PRjJJ2BVKG8qvM0yyYKZfT3lkoqSZUZsZifNbJuWH30dNbO1iUoqRsq/5HAGM9sBXCBpi6Tzq4+PSTplZjP5KssnayBSmKcQXixbV300a2Y/5qwppyIW8cxsq5mtrX4GG4ZUSCDufx5IYTyQwngghfFACuOBFMYDKYwHUhgPpDAeSGE8kMJ4IIXxQArjgRTGAymMB1IYD6QwHkhhPJDCeCCF8UCcc84555xzzjnnnHOuD/4DOnqEt7fS+RUAAAAASUVORK5CYII=";
        cropped = "iVBORw0KGgoAAAANSUhEUgAAADYAAAAkCAYAAADCW8lNAAAABHNCSVQICAgIfAhkiAAAAAFzUkdCAK7OHOkAAAAEZ0FNQQAAsY8L/GEFAAAACXBIWXMAAA7DAAAOwwHHb6hkAAAAIXRFWHRDcmVhdGlvbiBUaW1lADIwMjA6MDY6MDMgMjI6MTc6MThMAPB1AAACTklEQVRYR+2YSysGURjHX3ZCkqSUS1lIKazYUGz4Ar6CsvQJ+AZ8AxuyILeSKJdyLQsiZSNkLXaywf8/551pGs9cztze8+r91a85M413zjPnnOc8plChgi/v8Ec1/we18AUyKPoB62BZ0wwvoB2U7T1sgWVJGzyE3qBsr2AnLCu64QGUAnJ7CXthWdADN6AUiOQe7INGw6DWoRRAkLuwHxoJp5/OSHllcMaNHBNFlDUV5gnkCzICpvSg7Kcrgyt5tuTmK+1TSeVWUNJ9zl1RpO0rrIe58wylDqUpy69ceYJSR7KQxXPmcE3dQqkDWcopn1nhzJTORS09OA8fYOrZsgtyA5UemKep7nP8oW0oPagUshBIXDizfjNhpLwew0EYC46UiUHZHkHt2pJryqTp5yf/5Yk8LZn9TB4pr1xzoQmF+1TSlL4KFz3XgtS9X/IaBtaWSTffLdgKZ13XwpyH7NSS61oc+YFI3MSTlkmc702Q6AZGGiFHT7onqqxQLKqLR9ZjSXb1GzgO36yzeLAPk5CpPC7txaMTWFz4ljh9B6yzdBiFrDC+rTN92KfEgd3BLL5TjMBzGDc4JzB2UJesgrIZhmeqqQWXhRMY39CmakZiHw6pZqawXzuqGQneKy4Ld4bxkyndzn5+xMmKftTAqNmyAVp419gCXFZNkTU4DZNkP10+4RQM6xf7/mWd+cDNUqoE+DG0A0YhzRGzYb9WoPfvee1Pv6qKRy/8kQnVdDiFj6oZCgObU81Q+KZnVDMU1rFjqunAb5r8ouWiUPgFfnHOuZdn2fcAAAAASUVORK5CYII=";
        other="iVBORw0KGgoAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAABHNCSVQICAgIfAhkiAAABE5JREFUeJzt3DuIHVUcx/HfX416EdQFwSJEiIXYKWglq2InCloYiMbCFxLRwsQQWBXMQ1AEjYUPdA0miCISIQipbART2AhqmoAWimIUfMVgElkxX4szF8Mmu3fm7Nxzzsz8P7DNJTPnf/LbmTmPuSs555xzzjnnnHPOOeec6z7LXQAwI+niRR9/b2bkqGfQgMuBdznTbO7aBge4Enj/LGGM3Za7xsEA1gDvLRMGwHHgrty19h6wGtg9IYyx34ANuWvureqZsadmGGNf5K47pfNSNQSMJM1LuiNVm12ULBBJ+yTdnrA9txTgk4a3qbGfgOty198rwFeRYfwJrM5df69UYZyKDGTx7N2tBPG3KYBLctffG8AIOBAZxDFgTe4+9AZhnvFRZBjfAFfl7kNvEGbgeyLDOAxck7sPvUFYm6q7HLLYIeCW3H3oDcKq7aSFwqV8BtyUuw+9QXhmLLeEPimMm3P3oTeAGc6+uVTHIb8yWkQY2u6LDOMw/sxYUtSeOnBQUuw261FJX0Yem5WZTf0XqXEgwKeSbpxCLV1w+i/S52b2cNsNNAqkujJukHRO24V00N+Sfpb0tpk929ZJawdC2Lm7tq2Ge2a7me1o40QTAyHs9H2s+GfGkDwqad7M/o09QZ1bz05J18c2MDCvS1oHrIo9Qa1bFvCWpHsljWIbGpj1kvab2T9ND6z1cK5GE29IOtG0gYH6QNL9QON3FmqPlszsCYVLcqFpIwM1L6nxsDhmHvKcpCebHjdUZtbo/zh2pv68pLmYYyV9J2lv5LGp3S3p6hWeY87MXqj7j2Pfy3pG0q+SXow49kJJv5vZK5FtJ1OtSlxx2kd7I06zE7jMzLa2U9USqgXGTZELjEeAB6da4BQAdwIbIvr7baoCLwKejgzlB+C+JIW2CDgXeKhhX08Spg5JChwBWyJD+QN4IEmhLQJWAesb9jXdS+NVgXORoZwA7klWbEsIV0qT21f6t/iBbZGhQAe3cwnPlHIDqYp8DViIDGUWyP5F1CaAxxv0b/+k87W+r2Fmj0l6VdJfEYcflHQr0KX9ll8U9kXKBrxMGF3E6NT3SKh/q05/hYyZ2WZJ70hqvOIp6QAD/RbuVG8NZrZRcbN5SfqQDg6JV2rq92oze0rS9ohDR5J2ARvbrahsSR6e1X5zzArxpZIeabmcoqUczbyksCjplpEskGo7c5ekTana7KKk430zOy5pt6TNNQ85orA/PRjJJ2BVKG8qvM0yyYKZfT3lkoqSZUZsZifNbJuWH30dNbO1iUoqRsq/5HAGM9sBXCBpi6Tzq4+PSTplZjP5KssnayBSmKcQXixbV300a2Y/5qwppyIW8cxsq5mtrX4GG4ZUSCDufx5IYTyQwngghfFACuOBFMYDKYwHUhgPpDAeSGE8kMJ4IIXxQArjgRTGAymMB1IYD6QwHkhhPJDCeCCF8UCcc84555xzzjnnnHOuD/4DOnqEt7fS+RUAAAAASUVORK5CYII=";
        difference = difference(original, cropped);
    }
    @Test
    public void contains() {
        String[] set = {original,cropped};
        System.out.println(original.length());
        System.out.println(cropped.length());
        System.out.println();
        int si = StringUtils.indexOf(original, cropped);
        System.out.println("common prefix = "+StringUtils.getCommonPrefix(set)+" | "+StringUtils.getCommonPrefix(set).length()+" | "+si);
//        System.out.println("diff_length"+StringUtils.getLevenshteinDistance(difference,left_cropped));
//        System.out.println(difference);
        System.out.println();
        boolean contains = StringUtils.contains(original, cropped);
        boolean containsAny = StringUtils.containsAny(original, cropped);
        boolean containsOnly = StringUtils.containsOnly(original, cropped);
        boolean containsNone = StringUtils.containsNone(original, cropped);


        System.out.println(contains+","+containsAny+","+containsOnly+","+containsNone);
        assertNotEquals(original, cropped);
    }

    @Test
    public void similarity(){
        String odifference = StringUtils.difference(original,other);
        int levD = StringUtils.getLevenshteinDistance(original,other);

        double ratio = odifference.length()/original.length();
        System.out.println("Similarity test");
        System.out.println(original.length());
        System.out.println(other.length());
        System.out.println(ratio);
        System.out.println(odifference.length());
        System.out.println(levD);
        System.out.println(odifference);
        assertTrue(ratio > 0.85);

    }


    /*
    * -------Preparing IconDataset of icons-------
    * Store all image icons in 100x100 size
    * crop image and retain unique part of image
    * convert all those unique images to string (base64 encoding)
    * remove common prefix from all
    * save those strings with direction names: dataset ready
    *
    * -------Detecting icons-------
    * get the icon object from notification
    * convert it to bitmap and turn binary(remove RGB colors)
    * resize it to 100x100
    * extract bytes data from bitmap object
    * convert those bytes to base64 String
    * compare all the strings in dataset with the base64 string we got
    *       if any dataset string is a substring of our base64 string
    *               match found : return the name of the icon
    *
    *
     * */

}