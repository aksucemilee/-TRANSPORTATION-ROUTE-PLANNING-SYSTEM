package org.prolab2_1.prolab2_1.sistem.arac;

import org.prolab2_1.prolab2_1.sistem.Durak;
import org.prolab2_1.prolab2_1.sistem.NextStop;

public class Tramvay implements DurakArac {
    public Tramvay() {
    }

    //  hesaplamaları yap
    @Override
    public double UcretHesapla(Durak baslangic,Durak bitis) {
        if (baslangic == null || bitis == null) return 0.0;
        if (baslangic.getNextStops() != null) {
            for (NextStop ns : baslangic.getNextStops()) {
                // Durak ID eşleşmesi
                if (ns.getstopId().equalsIgnoreCase(bitis.getId())) {
                    return ns.getUcret(); // JSON'daki 'ucret'
                }
            }
        }
        return 0.0;
    }

    @Override
    public double SureHesapla(Durak baslangic,Durak bitis) {
        if (baslangic == null || bitis == null) return 0.0;
        if (baslangic.getNextStops() != null) {
            for (NextStop ns : baslangic.getNextStops()) {
                if (ns.getstopId().equalsIgnoreCase(bitis.getId())) {
                    return ns.getSure(); // JSON'daki 'sure'
                }
            }
        }
        return 0.0;
    }

    @Override
    public String getAracTuru() {
        return "Tramvay";
    }
}
