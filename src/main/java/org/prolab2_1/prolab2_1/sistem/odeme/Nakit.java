package org.prolab2_1.prolab2_1.sistem.odeme;

public class Nakit implements Odeme {
    private double nakit = 30.0;

    @Override
    public double odemeIndirimi(double tutar) {
        return tutar;
    }

    @Override
    public String getOdemeTuru() {
        return "Nakit";
    }

    @Override
    public boolean odemeYap(double tutar) {
        if (nakit >= tutar) {
            nakit -= tutar;
            return true;
        }
        return false;
    }

    @Override
    public double getBakiye() {
        return nakit;
    }
}
