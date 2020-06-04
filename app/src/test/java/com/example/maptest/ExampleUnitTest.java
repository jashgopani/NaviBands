package com.example.maptest;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    String original, cropped,difference;
    @Before
    public void init(){
        original = "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAABHNCSVQICAgIfAhkiAAAAxBJREFUeJzt3LFrE2EYx/Gv4iJRREopCLGFDrEgWCddLDj5Fzi5u7qJm3+C/gciCB0Uq0LRCq2CVoUOFmvBRWpolw6SbuJiHN6chiTX3L13996T3O8Dz2Lvcs/dj7u8zxEEERERERERERERCakFtMtuQqAGNHFhtIED4ESpHVXYJPCR/2FEtQ1MldhXJdWBNfrDiGoDmCmruappAKvEhxHVJ+B8ST1WxhywxPAwoloBLpTSaQXMAU9JHkZUL4H5Evodaw3S3RmDQtGdkpM6yb4zhtU7XLCSwSSHr6Z8QpkJeQLjpMbgOSNrbaA5xUv3BJ537QInw53K6PtBcWFEdRDqZEbdDsWHEVUr0DmNpBrwhXBhRNVELyT71HFftqHDiOobWn39M4sb3MoKIyrNKbgL8ILyw4hqlQq/kJzHxp3RW2+BS8Wdtk0NbIYR1Rsq9O5rFluPqbhaoQKPrzq274zeWmWMv+hrZF/aPgYeFLj9oPrMmL77yjr0PQfOAHdT7HMPdzEfZTz2NmM2PO6Q7YKsABOdz0obCMBp3N2SpYdmjtcj1tEAx2iRbQreBK4BPzP2cB23pPV1NsO+iYUIxFcb95i7mONnXsVN5H8892/n2MtAlgPZophZYAH4gH8ohQoRyJbnPkUOZleAdY/9NvNupFeIQBaAZym2fw1cLqiXbgvAcortl8n38Vm6pEvbibgP6PBZZcU5TvLV16mkJ5rFsRAH6biP+xXJjZi/PwFukW01ldYv4Cbwm8P72utsM3amGDw5LwHTCT8jzzuku6/FAfsvpugrFyHvEIB94A7988B7Ag1eMfaB28Crnn9fw/1CJZjQgYA7+YclHHeYXQz0ZXkOqSQFYowCMUaBGKNAjFEgxigQYxSIMQrEGAVijAIxRoEYo0CMUSDGKBBjFIiIiPg6UnYDHhrAuYTbfge+FtiLUMzPgMzQKssYBWKMAjFGgRijQIxRIMYoEBER8TWK77KmSf4fEezh3mdJgfQuS8JRIMYoEGMUiDEKxBgFYowCMUaBiIiIiIiIiIiIiJe/dpXuauNoqe4AAAAASUVORK5CYII=";
        cropped = "iVBORw0KGgoAAAANSUhEUgAAADYAAAAkCAYAAADCW8lNAAAABHNCSVQICAgIfAhkiAAAAAFzUkdCAK7OHOkAAAAEZ0FNQQAAsY8L/GEFAAAACXBIWXMAAA7DAAAOwwHHb6hkAAAAIXRFWHRDcmVhdGlvbiBUaW1lADIwMjA6MDY6MDMgMjI6MTc6MThMAPB1AAACTklEQVRYR+2YSysGURjHX3ZCkqSUS1lIKazYUGz4Ar6CsvQJ+AZ8AxuyILeSKJdyLQsiZSNkLXaywf8/551pGs9cztze8+r91a85M413zjPnnOc8plChgi/v8Ec1/we18AUyKPoB62BZ0wwvoB2U7T1sgWVJGzyE3qBsr2AnLCu64QGUAnJ7CXthWdADN6AUiOQe7INGw6DWoRRAkLuwHxoJp5/OSHllcMaNHBNFlDUV5gnkCzICpvSg7Kcrgyt5tuTmK+1TSeVWUNJ9zl1RpO0rrIe58wylDqUpy69ceYJSR7KQxXPmcE3dQqkDWcopn1nhzJTORS09OA8fYOrZsgtyA5UemKep7nP8oW0oPagUshBIXDizfjNhpLwew0EYC46UiUHZHkHt2pJryqTp5yf/5Yk8LZn9TB4pr1xzoQmF+1TSlL4KFz3XgtS9X/IaBtaWSTffLdgKZ13XwpyH7NSS61oc+YFI3MSTlkmc702Q6AZGGiFHT7onqqxQLKqLR9ZjSXb1GzgO36yzeLAPk5CpPC7txaMTWFz4ljh9B6yzdBiFrDC+rTN92KfEgd3BLL5TjMBzGDc4JzB2UJesgrIZhmeqqQWXhRMY39CmakZiHw6pZqawXzuqGQneKy4Ld4bxkyndzn5+xMmKftTAqNmyAVp419gCXFZNkTU4DZNkP10+4RQM6xf7/mWd+cDNUqoE+DG0A0YhzRGzYb9WoPfvee1Pv6qKRy/8kQnVdDiFj6oZCgObU81Q+KZnVDMU1rFjqunAb5r8ouWiUPgFfnHOuZdn2fcAAAAASUVORK5CYII=";
        difference = StringUtils.difference(original, cropped);
//        left = "LongStringWithProcessingOperation";
//        left_cropped = "ProcessingOperationWith";
    }
    @Test
    public void contains() {
        String[] set = {original,cropped};
        System.out.println(original.length());
        System.out.println(cropped.length());
        System.out.println();
        int si = StringUtils.indexOf(original, cropped);
        System.out.println("common prefix = "+StringUtils.getCommonPrefix(set)+" | "+StringUtils.getCommonPrefix(set).length());
//        System.out.println("diff_length"+StringUtils.getLevenshteinDistance(difference,left_cropped));
        System.out.println(difference);
        System.out.println();
        boolean contains = StringUtils.contains(original, cropped);
        boolean containsAny = StringUtils.containsAny(original, cropped);
        boolean containsOnly = StringUtils.containsOnly(original, cropped);
        boolean containsNone = StringUtils.containsNone(original, cropped);
//        assertTrue(contains);
//        assertTrue(containsAny);
//        assertFalse(containsOnly);
//        assertFalse(containsNone);

        System.out.println(contains+","+containsAny+","+containsOnly+","+containsNone);
        assertNotEquals(original, cropped);
    }

    /*
    * -------Preparing Dataset of icons-------
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