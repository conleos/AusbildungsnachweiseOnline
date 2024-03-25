package com.conleos.views.form;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class YearSelect {
    List<Year> years = new ArrayList<>();

    public YearSelect() {
        for (int i = -2; i<3;i++) {
            this.years.add(Year.now().minusYears(i));
        }
    }

}
