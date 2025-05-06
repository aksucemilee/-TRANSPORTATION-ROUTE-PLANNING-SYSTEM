package org.prolab2_1.prolab2_1.sistem.odeme;

public class Kredikart implements Odeme {
    private double limit = 200.0;

    @Override
    public double odemeIndirimi(double tutar) {
        return tutar * 1.15; // %15 zam
    }

    @Override
    public String getOdemeTuru() {
        return "Kredikart";
    }

    @Override
    public boolean odemeYap(double tutar) {
        if (limit >= tutar) {
            limit -= tutar;
            return true;
        }
        return false;
    }

    @Override
    public double getBakiye() {
        return limit;
    }
}
