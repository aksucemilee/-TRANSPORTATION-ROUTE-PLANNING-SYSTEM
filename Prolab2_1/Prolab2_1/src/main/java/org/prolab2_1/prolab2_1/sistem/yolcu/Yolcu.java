package org.prolab2_1.prolab2_1.sistem.yolcu;

public abstract class Yolcu {
    private String isim;
    private int yas;
    public Yolcu(String isim, int yas) {
        this.isim = isim;
        this.yas = yas;
    }
    public String getIsim() {
        return isim;
    }
    public int getYas() {
        return yas;
    }
    public abstract double indirimHesapla(double ucret,boolean taksiMi);
}
