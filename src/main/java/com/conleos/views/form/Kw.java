package com.conleos.views.form;

import java.util.ArrayList;
import java.util.List;

public class Kw {
    public List<Integer> calendarWeeks = new ArrayList<>();
    public Kw() {
        List<Integer>  calendarWeeks= new ArrayList<>();
        for (int i = 1; i<=52;i++) {
            calendarWeeks.add(i);
        }
        this.calendarWeeks = calendarWeeks;
    }


}
