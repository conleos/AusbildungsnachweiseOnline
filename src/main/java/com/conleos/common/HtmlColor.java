package com.conleos.common;

import com.conleos.i18n.Lang;

import java.awt.*;

public class HtmlColor {

    private int red;
    private int green;
    private int blue;
    private int alpha;

    public HtmlColor(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public HtmlColor(int red, int green, int blue, int alpha) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
        this.alpha = clamp(alpha);
    }

    public HtmlColor(String htmlColor) {
        parseHTMLColor(htmlColor);
    }
    public HtmlColor(Color awtColor) {
        this(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), awtColor.getAlpha());
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public Color toAWTColor() {
        return new Color(red, green, blue, alpha);
    }

    public String toHTMLString() {
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    private void parseHTMLColor(String htmlColor) {
        if (htmlColor.startsWith("#")) {
            htmlColor = htmlColor.substring(1);
        }

        if (htmlColor.length() == 6) {
            red = Integer.parseInt(htmlColor.substring(0, 2), 16);
            green = Integer.parseInt(htmlColor.substring(2, 4), 16);
            blue = Integer.parseInt(htmlColor.substring(4, 6), 16);
            alpha = 255;
        } else if (htmlColor.length() == 8) {
            alpha = Integer.parseInt(htmlColor.substring(0, 2), 16);
            red = Integer.parseInt(htmlColor.substring(2, 4), 16);
            green = Integer.parseInt(htmlColor.substring(4, 6), 16);
            blue = Integer.parseInt(htmlColor.substring(6, 8), 16);
        } else {
            throw new IllegalArgumentException(Lang.translate("view.Html.error"));
        }
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public static HtmlColor from(Color awtColor) {
        return new HtmlColor(awtColor);
    }

    @Override
    public String toString() {
        return toHTMLString();
    }

}