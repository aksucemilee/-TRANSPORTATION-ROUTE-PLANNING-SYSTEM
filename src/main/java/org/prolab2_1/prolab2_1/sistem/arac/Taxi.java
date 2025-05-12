package org.prolab2_1.prolab2_1.sistem.arac;

public class Taxi implements MesafeArac{
    private double openingFee;
    private double costPerKm;

    public Taxi() {}

    public Taxi(double openingFee, double costPerKm) {
        this.openingFee = openingFee;
        this.costPerKm = costPerKm;
    }

    public double getOpeningFee() {
        return openingFee;
    }
    public void setOpeningFee(double openingFee) {
        this.openingFee = openingFee;
    }
    public double getCostPerKm() {
        return costPerKm;
    }
    public void setCostPerKm(double costPerKm) {
        this.costPerKm = costPerKm;
    }

    @Override
    public double UcretHesapla(double mesafe) {
        return openingFee + costPerKm * mesafe;
    }

    @Override
    public double SureHesapla(double distance) {
        double ortalamaHizKmSaat = 40.0; //taksinin ort hızına göre süre hesaplaması
        return (distance / ortalamaHizKmSaat) * 60; // dakika
    }

    @Override
    public String getAracTuru(){
        return "Taxi";
    }
}