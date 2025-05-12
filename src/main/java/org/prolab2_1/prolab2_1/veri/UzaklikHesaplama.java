package org.prolab2_1.prolab2_1.veri;

public class UzaklikHesaplama {
    private static final double dunya_yaricap = 6371.0; // Dünya yarıçapı (ortalama) km
    private UzaklikHesaplama() {
        // Statik metodlar için private constructor
    }
    /*Haversinüs formülü, bir kürenin yüzeyindeki iki nokta arasındaki mesafeleri
     iki noktanın enlem ve boylamını kullanarak km cinsinden hesaplamanın  yoludur.
    Haversinüs formülü, kosinüslerin küresel yasasının yeniden formüle edilmiş halidir,
     ancak haversinüsler cinsinden formüle edilmesi küçük açılar ve mesafeler için daha kullanışlıdır.
     */
    public static double haversineUzaklik(double lat1, double lon1, double lat2, double lon2) {
        // Derece -> Radyan
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double uzaklik = dunya_yaricap * c;
        return uzaklik;
    }
}
