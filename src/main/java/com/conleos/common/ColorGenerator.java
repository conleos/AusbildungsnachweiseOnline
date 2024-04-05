package com.conleos.common;

import java.awt.*;
import java.util.Random;

public class ColorGenerator {

    private ColorGenerator() {

    }

    /*
    * Convert any String into a Color. This function is deterministic. Same String as an Input results in the same Color as an Output.
    * */
    public static Color fromRandomString(String str) {
        Random random = new Random(str.concat("ABCDefghiJK12345").hashCode());
        return new Color(random.nextInt(64, 255), random.nextInt(64, 255), random.nextInt(64, 255));
    }

}
