package com.conleos.views.form;

import com.conleos.i18n.Lang;

public enum KindOfWork {


    PracticalWork("kindOfWork.practicalWork"),
    Schooling("kindOfWork.schooling"),
    Vacation("kindOfWork.vacation"),
    Illness("kindOfWork.illness");


    private String label;

    private KindOfWork(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return Lang.translate(label);
    }
}
