package org.prolab2_1.prolab2_1.sistem;
import java.util.List;

import org.prolab2_1.prolab2_1.sistem.arac.Taxi;

public class SehirVerisi {
    private List<Durak> duraklar; //durak listesi
    private Taxi taxi; //jacksondan buraya "openingFree","costPerKm" alıncak
    private String city; // izmit

    public List<Durak> getDuraklar() {
        return duraklar;
    }
    public void setDuraklar(List<Durak> duraklar) {
        this.duraklar = duraklar;
    }

    public Taxi getTaxi() {
        return taxi;
    }
    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

}
