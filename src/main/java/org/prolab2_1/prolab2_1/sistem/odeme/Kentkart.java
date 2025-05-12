package org.prolab2_1.prolab2_1.sistem.odeme;

public class Kentkart implements Odeme {
    private double bakiye = 50.0; // örnek bakiye

    @Override
    public double odemeIndirimi(double tutar) {
        return tutar;
    }

    @Override
    public String getOdemeTuru() {
        return "Kentkart";
    }

    @Override
    public boolean odemeYap(double tutar) {
        if (bakiye >= tutar) {
            bakiye -= tutar;
            return true;
        }
        return false;
    }

    @Override
    public double getBakiye() {
        return bakiye;
    }
}
