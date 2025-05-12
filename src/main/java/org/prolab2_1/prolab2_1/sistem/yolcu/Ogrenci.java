package org.prolab2_1.prolab2_1.sistem.yolcu;

public class Ogrenci extends Yolcu {
    public Ogrenci(String isim,int yas){
        super(isim,yas);
    }
    @Override
    public double indirimHesapla(double ucret,boolean taksiMi) {
        if(taksiMi){
            return ucret;
        }
        else{
            return ucret * 0.50;
        }
    }
}
