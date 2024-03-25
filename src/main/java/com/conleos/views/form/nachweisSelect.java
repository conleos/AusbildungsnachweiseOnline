package com.conleos.views.form;

import java.util.ArrayList;
import java.util.List;

public class nachweisSelect {
    public List<Integer> nachweisNr = new ArrayList<>();
    public nachweisSelect() {
        List<Integer>  nachweisNr= new ArrayList<>();
        for (int i = 1; i<=160;i++) {
            nachweisNr.add(i);
        }
        this.nachweisNr = nachweisNr;
    }
}
