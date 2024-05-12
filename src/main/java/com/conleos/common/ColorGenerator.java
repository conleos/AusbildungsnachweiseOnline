package com.conleos.common;

import com.conleos.data.entity.FormStatus;
import jdk.jshell.Snippet;

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

    public static Color statusColor(FormStatus status) {
        Color color = Color.BLUE;
        switch (status) {
            case InProgress:
                color = new Color(112,114,255);
                break;
            case InReview:
                color = new Color(255,185,49);
                break;
            case Rejected:
                color = new Color(255,66,76);
                break;
            case Signed:
                color = new Color(99,225,99);
                break;
        }
        return color;
    }

}
