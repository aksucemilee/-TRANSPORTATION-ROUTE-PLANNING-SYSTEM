package org.prolab2_1.prolab2_1.sistem.arac;

import org.prolab2_1.prolab2_1.sistem.Durak;
import org.prolab2_1.prolab2_1.sistem.NextStop;

public class Otobus implements DurakArac {
    public Otobus() {
    }

    //  hesaplamaları yap
    @Override
    public double UcretHesapla(Durak baslangic,Durak bitis) {
        if(baslangic==null || bitis==null) return 0.0;
        if(baslangic.getNextStops() != null){
            for(NextStop nextStop : baslangic.getNextStops()){
                if(nextStop.getstopId().equalsIgnoreCase(bitis.getId())){
                    return nextStop.getUcret();
                }
            }
        }
        return 0.0;
    }

    @Override
    public double SureHesapla(Durak baslangic,Durak bitis) {
        if(baslangic==null || bitis==null) return 0.0;
        if(baslangic.getNextStops() != null){
            for(NextStop nextStop : baslangic.getNextStops()){
                if(nextStop.getstopId().equalsIgnoreCase(bitis.getId())){
                    return nextStop.getSure();
                }
            }
        }
        return 0.0;
    }

    @Override
    public String getAracTuru() {
        return "Otobus";
    }
}
