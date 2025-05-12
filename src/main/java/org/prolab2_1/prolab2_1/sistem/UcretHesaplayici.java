package org.prolab2_1.prolab2_1.sistem;

import org.prolab2_1.prolab2_1.sistem.yolcu.Yolcu;
import org.prolab2_1.prolab2_1.sistem.odeme.Odeme;
import org.prolab2_1.prolab2_1.sistem.arac.DurakArac;
import org.prolab2_1.prolab2_1.sistem.arac.MesafeArac;

public class UcretHesaplayici {

    // Durak bazlı (otobüs, tramvay) hesaplama
    public static double hesapla(DurakArac arac, Durak baslangic, Durak bitis, Yolcu yolcu, Odeme odeme) {
        double tabanUcret = arac.UcretHesapla(baslangic, bitis);
        double yolcuIndirimliHali = yolcu.indirimHesapla(tabanUcret, false);

        return odeme.odemeIndirimi(yolcuIndirimliHali);
    }

    // Mesafe bazlı (taksi) hesaplama
    public static double hesapla(MesafeArac arac, double mesafe, Yolcu yolcu, Odeme odeme) {
        return arac.UcretHesapla(mesafe);
    }
}
