package com.conleos.views.form;

public enum KindOfWork {


    PracticalWork("Betrieb"),
    Schooling("Schule");

    private String label;
    private KindOfWork(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return label;
    }
}
