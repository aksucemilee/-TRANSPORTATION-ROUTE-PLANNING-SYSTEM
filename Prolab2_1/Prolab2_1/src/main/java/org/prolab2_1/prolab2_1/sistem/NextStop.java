package org.prolab2_1.prolab2_1.sistem;

//duraklar arası graf veya baglantı listesi olusturulurken bu sınıf kullanılır
//her durak nesnesi genellikle list<nextstop> tutar ordan verilere ulasılabilir.
public class NextStop {
    private String stopId; //"bus_sekapark" falan
    private double mesafe; //km
    private int sure; //dakika
    private double ucret; //tl cinsi

    public NextStop(){
    }
    public NextStop(String stopId, double mesafe, int sure, double ucret) {
        this.stopId = stopId;
        this.mesafe = mesafe;
        this.sure = sure;
        this.ucret = ucret;
    }
    public String getstopId() {
        return stopId;
    }
    public void setstopId(String stopId) {
        this.stopId = stopId;
    }

    public double getMesafe() {
        return mesafe;
    }
    public void setMesafe(double mesafe) {
        this.mesafe = mesafe;
    }

    public int getSure() {
        return sure;
    }
    public void setSure(int sure) {
        this.sure = sure;
    }

    public double getUcret() {
        return ucret;
    }
    public void setUcret(double ucret) {
        this.ucret = ucret;
    }
}
