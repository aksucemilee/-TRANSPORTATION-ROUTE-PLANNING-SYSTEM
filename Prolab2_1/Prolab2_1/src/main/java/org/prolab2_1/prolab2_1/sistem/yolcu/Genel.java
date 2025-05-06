package org.prolab2_1.prolab2_1.sistem.yolcu;

public class Genel extends Yolcu {
    public Genel(String isim,int yas){
        super(isim,yas);
    }
    @Override
    public double indirimHesapla(double ucret,boolean taksiMi) {
        return ucret;
    }
}
