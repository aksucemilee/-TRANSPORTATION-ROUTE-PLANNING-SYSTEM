package org.prolab2_1.prolab2_1.veri;

import org.prolab2_1.prolab2_1.sistem.Durak;
import org.prolab2_1.prolab2_1.sistem.SehirVerisi;
import org.prolab2_1.prolab2_1.sistem.UcretHesaplayici;
import org.prolab2_1.prolab2_1.sistem.arac.*;
import org.prolab2_1.prolab2_1.sistem.odeme.Kentkart;
import org.prolab2_1.prolab2_1.sistem.odeme.Odeme;
import org.prolab2_1.prolab2_1.sistem.yolcu.Yolcu;

import java.util.List;

public class RotaHesaplama {
    private static final double esikMesafe = 3.0;       // 3km eşik
    private static final double YURUME_HIZI_KMH = 3.0;  // 3 km/s yürüme

    private RotaHesaplama() {
    }

    // ---------------------------------------------------------
    // 1) Temel yardımcı fonksiyonlar (değişmeden kalıyor)
    // ---------------------------------------------------------

    public static Durak enYakinDurak(double kullaniciLat, double kullaniciLon, List<Durak> duraklar) {
        Durak enYakin = null;
        double minUzaklik = Double.MAX_VALUE;

        for (Durak d : duraklar) {
            double uzaklik = UzaklikHesaplama.haversineUzaklik(
                    kullaniciLat, kullaniciLon,
                    d.getLat(), d.getLon()
            );
            if (uzaklik < minUzaklik) {
                minUzaklik = uzaklik;
                enYakin = d;
            }
        }
        return enYakin;
    }

    public static boolean esikKontrol(double mesafe) {
        return mesafe > esikMesafe;
    }

    public static double yurumeSuresi(double mesafeKm) {
        // 3 km/s yürüyüş hızına göre dk hesap
        return (mesafeKm / YURUME_HIZI_KMH) * 60.0;
    }

    public static double otobusUcretiHesaplama(SehirVerisi cityData, Durak baslangic, Durak bitis) {
        if (baslangic == null || bitis == null) return 0.0;
        Otobus otobus = new Otobus();
        return otobus.UcretHesapla(baslangic, bitis);
    }

    public static double otobusSureHesaplama(SehirVerisi cityData, Durak baslangic, Durak bitis) {
        Otobus otobus = new Otobus();
        return otobus.SureHesapla(baslangic, bitis);
    }

    public static double tramvayUcretiHesaplama(SehirVerisi cityData, Durak baslangic, Durak bitis) {
        if (baslangic == null || bitis == null) return 0.0;
        Tramvay tramvay = new Tramvay();
        return tramvay.UcretHesapla(baslangic, bitis);
    }

    public static double tramvaySureHesaplama(SehirVerisi cityData, Durak baslangic, Durak bitis) {
        Tramvay tramvay = new Tramvay();
        return tramvay.SureHesapla(baslangic, bitis);
    }

    public static List<Durak> enAzAktarmaRota(Durak baslangic, Durak bitis, List<Durak> duraklar) {
        return GrafAlgoritmasi.bfsYolBulma(baslangic, bitis, duraklar);
    }

    public static List<Durak> enKisaSureRota(Durak bas, Durak hedef, List<Durak> duraklar) {
        return GrafAlgoritmasi.dijkstraSureRota(bas, hedef, duraklar);
    }

    // ---------------------------------------------------------
    // 2) Taksi hesaplama (yolcu + ödeme)
    // ---------------------------------------------------------
    public static double taksiUcretiHesaplama(SehirVerisi cityData,
                                              double mesafeKm,
                                              Yolcu yolcu,
                                              Odeme odeme) {
        Taxi taxi = cityData.getTaxi();
        if (taxi == null) return 0.0;
        return UcretHesaplayici.hesapla(taxi, mesafeKm, yolcu, odeme);
    }

    // ---------------------------------------------------------
    // 3) BFS/Dijkstra sonucu path üzerindeki
    //    otobüs/tram/transfer sure+ucret + aktarma sayısı
    // ---------------------------------------------------------
    /**
     * Proje kuralına göre:
     * - "bus" veya "tram" -> "bus" veya "tram" her adımda +1 aktarma.
     * - "taxi" (veya type bilinmiyorsa) -> "bus/tram" = 0 aktarma.
     * - "transfer" JSON alanında aktarmaSayisi++ eklenmedi,
     *   çünkü bus->tram yine en alttaki if ile +1 olacak.
     */

    public static RotaSonucu hesaplaYolu(List<Durak> yol,
                                         SehirVerisi cityData,
                                         Yolcu yolcu,
                                         Odeme odeme) {
        if (yol == null || yol.size() < 2) {
            return new RotaSonucu(0, 0, 0);
        }

        double totalSure  = 0.0;
        double totalUcret = 0.0;
        int aktarmaSayisi = 0;

        for (int i = 0; i < yol.size() - 1; i++) {
            Durak simdi = yol.get(i);
            Durak sonra = yol.get(i + 1);

            // Transfer varsa özel işlem
            if (simdi.getTransfer() != null &&
                    simdi.getTransfer().getTransferStopId().equalsIgnoreCase(sonra.getId())) {

                double transferSure = simdi.getTransfer().getTransferSure();
                double transferUcret = simdi.getTransfer().getTransferUcret();

                totalSure += transferSure;
                double indirimli = yolcu.indirimHesapla(transferUcret, false);
                totalUcret += odeme.odemeIndirimi(indirimli);
            } else {
                Arac arac = aracBul(simdi, sonra);

                if (arac instanceof DurakArac dArac) {
                    double sure = dArac.SureHesapla(simdi, sonra);
                    double ucret = dArac.UcretHesapla(simdi, sonra);

                    boolean taksiMi = arac.getAracTuru().equalsIgnoreCase("Taxi");

                    if (taksiMi) {
                        totalUcret += ucret;
                    } else {
                        double yolcuInd = yolcu.indirimHesapla(ucret, false);
                        totalUcret += odeme.odemeIndirimi(yolcuInd);
                    }

                    totalSure += sure;
                }
            }

            // Aktarma sayısı hesapla (bus ↔ tram)
            String t1 = (simdi.getType() == null) ? "" : simdi.getType().toLowerCase();
            String t2 = (sonra.getType() == null) ? "" : sonra.getType().toLowerCase();
            boolean currToplu = t1.equals("bus") || t1.equals("tram");
            boolean nextToplu = t2.equals("bus") || t2.equals("tram");
            if (currToplu && nextToplu) {
                aktarmaSayisi++;
            }
        }

        return new RotaSonucu(totalSure, totalUcret, aktarmaSayisi);
    }


    // ---------------------------------------------------------
    // 4) “Detaylı Rota Metni” - PDF örneğine benzer format
    // ---------------------------------------------------------
    /**
     * Eğer metin çıktısını da "bus/tram -> bus/tram = +1" kuralıyla
     * göstermek istersen, transfer'deki aktarmaSayisi++'ı
     * kaldırdık. Döngü sonunda check koyduk.
     */
    public static String detayliRotaMetni(List<Durak> rota,
                                          SehirVerisi cityData,
                                          Yolcu yolcu,
                                          Odeme odeme) {
        if (rota == null || rota.size() < 2) {
            return "Rota bulunamadı veya eksik.\n";
        }

        StringBuilder sb = new StringBuilder();
        double toplamSure  = 0.0;
        double toplamUcret = 0.0;
        int aktarimSayisi  = 0;

        sb.append("🚏 Rota Detayları:\n");

        for (int i = 0; i < rota.size() - 1; i++) {
            Durak current = rota.get(i);
            Durak next    = rota.get(i + 1);

            sb.append("\n").append(i + 1).append("⃣ ")
                    .append(current.getName()).append(" → ")
                    .append(next.getName()).append(" ");

            double stepSure = 0.0;
            double stepUcret = 0.0;

            // Transfer kontrolü
            if (current.getTransfer() != null &&
                    current.getTransfer().getTransferStopId().equalsIgnoreCase(next.getId())) {

                sb.append("(🔄 Transfer)\n");
                stepSure = current.getTransfer().getTransferSure();
                double rawUcret = current.getTransfer().getTransferUcret();

                double yolcuInd = yolcu.indirimHesapla(rawUcret, false);
                stepUcret = odeme.odemeIndirimi(yolcuInd);
            } else {
                Arac arac = aracBul(current, next);

                if (arac instanceof DurakArac dArac) {
                    sb.append("(").append(arac.getAracTuru()).append(")\n");

                    stepSure = dArac.SureHesapla(current, next);
                    double rawUcret = dArac.UcretHesapla(current, next);

                    boolean taksiMi = arac.getAracTuru().equalsIgnoreCase("Taxi");

                    if (taksiMi) {
                        stepUcret = rawUcret;
                    } else {
                        double yolcuInd = yolcu.indirimHesapla(rawUcret, false);
                        stepUcret = odeme.odemeIndirimi(yolcuInd);
                    }
                } else {
                    sb.append("(🚫 Bilinmeyen Araç)\n");
                }
            }

            sb.append("   ⏳ Süre: ").append(String.format("%.1f", stepSure)).append(" dk\n");
            sb.append("   💰 Ücret: ").append(String.format("%.2f", stepUcret)).append(" TL\n");

            toplamSure += stepSure;
            toplamUcret += stepUcret;

            // Aktarma
            String t1 = (current.getType() == null) ? "" : current.getType().toLowerCase();
            String t2 = (next.getType()    == null) ? "" : next.getType().toLowerCase();
            boolean currToplu = t1.equals("bus") || t1.equals("tram");
            boolean nextToplu = t2.equals("bus") || t2.equals("tram");
            if (currToplu && nextToplu) {
                aktarimSayisi++;
            }
        }

        sb.append("\n📊 Toplam:\n");
        sb.append("● ⏳ Süre: ").append(String.format("%.1f", toplamSure)).append(" dk\n");
        sb.append("● 💰 Ücret: ").append(String.format("%.2f", toplamUcret)).append(" TL\n");
        sb.append("● 🔄 Aktarma Sayısı: ").append(aktarimSayisi).append("\n");

        return sb.toString();
    }

    public static String detayliSadeceOtobusRota(double basLat, double basLon,
                                                 double hedefLat, double hedefLon,
                                                 List<Durak> duraklar,
                                                 SehirVerisi cityData,
                                                 Yolcu yolcu,
                                                 Odeme odeme)
    {
        StringBuilder sb = new StringBuilder();

        // 1) En yakın duraklar
        Durak basEnYakin   = enYakinDurak(basLat, basLon, duraklar);
        Durak hedefEnYakin = enYakinDurak(hedefLat, hedefLon, duraklar);
        if (basEnYakin == null || hedefEnYakin == null) {
            return sb.append("Yakın durak bulunamadı, rota yok.\n").toString();
        }

        double distUserToDurak = UzaklikHesaplama.haversineUzaklik(basLat, basLon,
                basEnYakin.getLat(), basEnYakin.getLon());
        boolean userTaksi = esikKontrol(distUserToDurak);

        double distDurakToHedef = UzaklikHesaplama.haversineUzaklik(
                hedefEnYakin.getLat(), hedefEnYakin.getLon(),
                hedefLat, hedefLon
        );
        boolean targetTaksi = esikKontrol(distDurakToHedef);

        // 2) BFS onlyBus
        List<Durak> onlyBusRoute = GrafAlgoritmasi.bfsOnlyBus(basEnYakin, hedefEnYakin, duraklar);
        if (onlyBusRoute == null) {
            sb.append("Otobüs rotası bulunamadı.\n");
            return sb.toString();
        }

        double totalSure   = 0.0;
        double totalUcret  = 0.0;
        double totalMesafe = 0.0;

        // (A) Kullanıcı -> ilk durak
        Durak firstStop = onlyBusRoute.get(0);
        if (userTaksi) {
            double takSure = 0.0;
            if (cityData.getTaxi() != null) {
                takSure = cityData.getTaxi().SureHesapla(distUserToDurak);
            }
            double takUcret = taksiUcretiHesaplama(cityData, distUserToDurak, yolcu, odeme);

            sb.append(String.format("📍 Kullanıcı -> '%s' (Otobüs) : TAKSİ (%.2f km, ~%.1f dk, %.2f TL)\n\n",
                    firstStop.getName(), distUserToDurak, takSure, takUcret));

            totalSure   += takSure;
            totalUcret  += takUcret;
            totalMesafe += distUserToDurak;
        } else {
            double walkSure = yurumeSuresi(distUserToDurak);
            sb.append(String.format("📍 Kullanıcı -> '%s' (Otobüs) : YÜRÜME (%.2f km, ~%.1f dk)\n\n",
                    firstStop.getName(), distUserToDurak, walkSure));

            totalSure   += walkSure;
            totalMesafe += distUserToDurak;
        }

        // (B) Otobüs Hattı
        sb.append("🚌 Otobüs Hattı (BFS):\n");
        double busRouteSure  = 0.0;
        double busRouteUcret = 0.0;
        double busRouteMesafe= 0.0;

        for (int i = 0; i < onlyBusRoute.size() - 1; i++) {
            Durak curr = onlyBusRoute.get(i);
            Durak nxt  = onlyBusRoute.get(i+1);

            // Otobüs nextStop
            Otobus otobus = new Otobus();
            double sure  = otobus.SureHesapla(curr, nxt);
            double ucret = otobus.UcretHesapla(curr, nxt);

            double userDisc  = yolcu.indirimHesapla(ucret, false);
            double finalCost = odeme.odemeIndirimi(userDisc);

            double mesafe    = 0.0; // optional

            sb.append(String.format("   %s -> %s : Süre=%.1f dk, Ücret=%.2f TL\n",
                    curr.getName(), nxt.getName(), sure, finalCost));

            busRouteSure   += sure;
            busRouteUcret  += finalCost;
            busRouteMesafe += mesafe;
        }

        totalSure   += busRouteSure;
        totalUcret  += busRouteUcret;
        totalMesafe += busRouteMesafe;

        // (C) Son durak -> hedef
        Durak lastStop = onlyBusRoute.get(onlyBusRoute.size()-1);
        if (targetTaksi) {
            double takSure = 0.0;
            if (cityData.getTaxi() != null) {
                takSure = cityData.getTaxi().SureHesapla(distDurakToHedef);
            }
            double takUcret = taksiUcretiHesaplama(cityData, distDurakToHedef, yolcu, odeme);

            sb.append(String.format("\n📍 '%s' (Otobüs) -> Hedef : TAKSİ (%.2f km, ~%.1f dk, %.2f TL)\n",
                    lastStop.getName(), distDurakToHedef, takSure, takUcret));

            totalSure   += takSure;
            totalUcret  += takUcret;
            totalMesafe += distDurakToHedef;
        } else {
            double walkSure = yurumeSuresi(distDurakToHedef);
            sb.append(String.format("\n📍 '%s' (Otobüs) -> Hedef : YÜRÜME (%.2f km, ~%.1f dk)\n",
                    lastStop.getName(), distDurakToHedef, walkSure));

            totalSure   += walkSure;
            totalMesafe += distDurakToHedef;
        }

        sb.append(String.format("\n🧭 Toplam Durak: %d\n", onlyBusRoute.size()));
        sb.append(String.format("🕒 Toplam Süre: %.1f dk\n", totalSure));
        sb.append(String.format("💰 Toplam Ücret: %.2f TL\n", totalUcret));
        sb.append(String.format("📐 Toplam Mesafe: ~%.2f km (tahmini)\n", totalMesafe));

        return sb.toString();
    }

    /**
     * "Sadece Tramvay" BFS rotası + Kullanıcı->Durak + Durak->Hedef
     */
    public static String detayliSadeceTramvayRota(double basLat, double basLon,
                                                  double hedefLat, double hedefLon,
                                                  List<Durak> duraklar,
                                                  SehirVerisi cityData,
                                                  Yolcu yolcu,
                                                  Odeme odeme)
    {
        StringBuilder sb = new StringBuilder();

        Durak basEnYakin   = enYakinDurak(basLat, basLon, duraklar);
        Durak hedefEnYakin = enYakinDurak(hedefLat, hedefLon, duraklar);
        if (basEnYakin == null || hedefEnYakin == null) {
            return sb.append("Yakın durak bulunamadı, rota yok.\n").toString();
        }

        double distUserToDurak = UzaklikHesaplama.haversineUzaklik(
                basLat, basLon,
                basEnYakin.getLat(), basEnYakin.getLon()
        );
        boolean userTaksi = esikKontrol(distUserToDurak);

        double distDurakToHedef = UzaklikHesaplama.haversineUzaklik(
                hedefEnYakin.getLat(), hedefEnYakin.getLon(),
                hedefLat, hedefLon
        );
        boolean targetTaksi = esikKontrol(distDurakToHedef);

        List<Durak> onlyTramRoute = GrafAlgoritmasi.bfsOnlyTram(basEnYakin, hedefEnYakin, duraklar);
        if (onlyTramRoute == null) {
            sb.append("Tramvay rotası bulunamadı.\n");
            return sb.toString();
        }

        double totalSure   = 0.0;
        double totalUcret  = 0.0;
        double totalMesafe = 0.0;

        // Kullanıcı->durak
        Durak firstStop = onlyTramRoute.get(0);
        if (userTaksi) {
            double takSure = 0.0;
            if (cityData.getTaxi() != null) {
                takSure = cityData.getTaxi().SureHesapla(distUserToDurak);
            }
            double takUcret = taksiUcretiHesaplama(cityData, distUserToDurak, yolcu, odeme);
            sb.append(String.format("📍 Kullanıcı -> '%s' (Tramvay) : TAKSİ (%.2f km, ~%.1f dk, %.2f TL)\n\n",
                    firstStop.getName(), distUserToDurak, takSure, takUcret));

            totalSure   += takSure;
            totalUcret  += takUcret;
            totalMesafe += distUserToDurak;
        } else {
            double walkSure = yurumeSuresi(distUserToDurak);
            sb.append(String.format("📍 Kullanıcı -> '%s' (Tramvay) : YÜRÜME (%.2f km, ~%.1f dk)\n\n",
                    firstStop.getName(), distUserToDurak, walkSure));

            totalSure   += walkSure;
            totalMesafe += distUserToDurak;
        }

        // Tramvay Hattı
        sb.append("🚋 Tramvay Hattı (BFS):\n");
        double tramRouteSure  = 0.0;
        double tramRouteUcret = 0.0;
        double tramRouteMesafe= 0.0;

        for (int i = 0; i < onlyTramRoute.size() - 1; i++) {
            Durak curr = onlyTramRoute.get(i);
            Durak nxt  = onlyTramRoute.get(i+1);

            Tramvay tram = new Tramvay();
            double sure  = tram.SureHesapla(curr, nxt);
            double ucret = tram.UcretHesapla(curr, nxt);

            double userDisc  = yolcu.indirimHesapla(ucret, false);
            double finalCost = odeme.odemeIndirimi(userDisc);

            double mesafe    = 0.0;

            sb.append(String.format("   %s -> %s : Süre=%.1f dk, Ücret=%.2f TL\n",
                    curr.getName(), nxt.getName(), sure, finalCost));

            tramRouteSure   += sure;
            tramRouteUcret  += finalCost;
            tramRouteMesafe += mesafe;
        }

        totalSure   += tramRouteSure;
        totalUcret  += tramRouteUcret;
        totalMesafe += tramRouteMesafe;

        // Son durak-> hedef
        Durak lastStop = onlyTramRoute.get(onlyTramRoute.size()-1);
        if (targetTaksi) {
            double takSure = 0.0;
            if (cityData.getTaxi() != null) {
                takSure = cityData.getTaxi().SureHesapla(distDurakToHedef);
            }
            double takUcret = taksiUcretiHesaplama(cityData, distDurakToHedef, yolcu, odeme);
            sb.append(String.format("\n📍 '%s' (Tramvay) -> Hedef : TAKSİ (%.2f km, ~%.1f dk, %.2f TL)\n",
                    lastStop.getName(), distDurakToHedef, takSure, takUcret));

            totalSure   += takSure;
            totalUcret  += takUcret;
            totalMesafe += distDurakToHedef;
        } else {
            double walkSure = yurumeSuresi(distDurakToHedef);
            sb.append(String.format("\n📍 '%s' (Tramvay) -> Hedef : YÜRÜME (%.2f km, ~%.1f dk)\n",
                    lastStop.getName(), distDurakToHedef, walkSure));

            totalSure   += walkSure;
            totalMesafe += distDurakToHedef;
        }

        sb.append(String.format("\n🧭 Toplam Durak: %d\n", onlyTramRoute.size()));
        sb.append(String.format("🕒 Toplam Süre: %.1f dk\n", totalSure));
        sb.append(String.format("💰 Toplam Ücret: %.2f TL\n", totalUcret));
        sb.append(String.format("📐 Toplam Mesafe: ~%.2f km\n", totalMesafe));

        return sb.toString();
    }

    // ---------------------------------------------------------
    // BFS üzerinden en az aktarmalı rota vs.
    // ---------------------------------------------------------
    public static List<Durak> enAzAktarmaRotası(Durak baslangic, Durak hedef, List<Durak> duraklar) {
        return GrafAlgoritmasi.bfsYolBulma(baslangic, hedef, duraklar);
    }

    public static Arac aracBul(Durak from, Durak to) {
        String type = (from.getType() == null) ? "" : from.getType().toLowerCase();
        return switch (type) {
            case "bus" -> new Otobus();
            case "tram" -> new Tramvay();
            case "taxi" -> new Taxi(); // taxi durağı varsa
            default -> null;
        };
    }

}
