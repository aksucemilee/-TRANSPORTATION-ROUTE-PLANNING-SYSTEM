package org.prolab2_1.prolab2_1.sistem.odeme;

public interface Odeme {
    double odemeIndirimi(double tutar);
    String getOdemeTuru();

    // ✅ Yeni metodlar:
    boolean odemeYap(double tutar);     // tutarı düş, yeterli değilse false
    double getBakiye();                 // kalan bakiye
}
