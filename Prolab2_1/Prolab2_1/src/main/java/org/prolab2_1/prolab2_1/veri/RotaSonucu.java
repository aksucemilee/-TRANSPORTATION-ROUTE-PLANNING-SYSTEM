package org.prolab2_1.prolab2_1.veri;

//kucuk bi POJO olusturmak 'toplam süre, toplam ucret, aktarma sayyısı' gibi birbiriyle ilişkili sonucları tek nesnede dondurmememizi saglar
public class RotaSonucu {
    private double sure;
    private double ucret;
    private int aktarmaSayisi;

    public RotaSonucu(double sure, double ucret,int aktarmaSayisi) {
        this.sure = sure;
        this.ucret = ucret;
        this.aktarmaSayisi = aktarmaSayisi;
    }
    public double getSure() {
        return sure;
    }
    public void setSure(double sure) {
        this.sure = sure;
    }
    public double getUcret() {
        return ucret;
    }
    public void setUcret(double ucret) {
        this.ucret = ucret;
    }
    public int getAktarmaSayisi() {
        return aktarmaSayisi;
    }
    public void setAktarmaSayisi(int aktarmaSayisi) {
        this.aktarmaSayisi = aktarmaSayisi;
    }
}
