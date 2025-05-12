package org.prolab2_1.prolab2_1.sistem.arac;
import org.prolab2_1.prolab2_1.sistem.Durak;

public interface DurakArac extends Arac{
    double UcretHesapla(Durak baslangic,Durak bitis);
    double SureHesapla(Durak baslangic,Durak bitis);
}
