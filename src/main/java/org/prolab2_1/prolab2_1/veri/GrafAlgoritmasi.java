package org.prolab2_1.prolab2_1.veri;

import org.prolab2_1.prolab2_1.sistem.Durak;
import org.prolab2_1.prolab2_1.sistem.NextStop;

import java.util.*;

/**
 * Bu sınıf, şehirdeki duraklar arasında rota bulma algoritmalarını içerir:
 * - BFS: En az adımlı rota (genel veya filtreli)
 * - Dijkstra: En kısa süreli rota (genel veya filtreli)
 */
public class GrafAlgoritmasi {

    private GrafAlgoritmasi() {} // Statik yardımcı sınıf

    // ------------------------------------------------------------
    // 1) BFS - Genel Amaçlı
    // ------------------------------------------------------------
    public static List<Durak> bfsYolBulma(Durak baslangic, Durak hedef, List<Durak> allDuraks) {
        if (baslangic == null || hedef == null) {
            System.err.println("🚫 BFS Hatası: Başlangıç veya hedef durak null.");
            return null;
        }

        Map<String, Durak> oncekiDurak = new HashMap<>();
        Map<String, Boolean> ziyaretEdildi = new HashMap<>();

        for (Durak d : allDuraks) {
            ziyaretEdildi.put(d.getId(), false);
            oncekiDurak.put(d.getId(), null);
        }

        Queue<Durak> sira = new LinkedList<>();
        ziyaretEdildi.put(baslangic.getId(), true);
        sira.add(baslangic);

        while (!sira.isEmpty()) {
            Durak simdiki = sira.poll();

            if (simdiki.getId().equalsIgnoreCase(hedef.getId())) {
                return izlenilenYol(oncekiDurak, hedef);
            }

            // Komşu durakları sıraya ekle
            if (simdiki.getNextStops() != null) {
                for (NextStop nextStop : simdiki.getNextStops()) {
                    Durak komsu = DurakBulId(nextStop.getstopId(), allDuraks);
                    if (komsu != null && !ziyaretEdildi.get(komsu.getId()) && usableStop(komsu)) {
                        ziyaretEdildi.put(komsu.getId(), true);
                        oncekiDurak.put(komsu.getId(), simdiki);
                        sira.add(komsu);
                    }
                }
            }

            // Transfer varsa kontrol et
            if (simdiki.getTransfer() != null) {
                Durak transferDurak = DurakBulId(simdiki.getTransfer().getTransferStopId(), allDuraks);
                if (transferDurak != null && !ziyaretEdildi.get(transferDurak.getId()) && usableStop(transferDurak)) {
                    ziyaretEdildi.put(transferDurak.getId(), true);
                    oncekiDurak.put(transferDurak.getId(), simdiki);
                    sira.add(transferDurak);
                }
            }
        }

        System.out.println("❗ BFS sonucu: Rota bulunamadı.");
        return null;
    }

    // ------------------------------------------------------------
    // 2) BFS - Filtreli (Sadece "bus" veya "tram")
    // ------------------------------------------------------------
    public static List<Durak> bfsYolBulmaTypeFilter(Durak baslangic, Durak hedef, List<Durak> allDuraks, String tip) {
        if (baslangic == null || hedef == null || tip == null) return null;
        if (!usableStopType(baslangic, tip)) return null;

        Map<String, Durak> oncekiDurak = new HashMap<>();
        Map<String, Boolean> ziyaretEdildi = new HashMap<>();

        for (Durak d : allDuraks) {
            ziyaretEdildi.put(d.getId(), false);
            oncekiDurak.put(d.getId(), null);
        }

        Queue<Durak> sira = new LinkedList<>();
        ziyaretEdildi.put(baslangic.getId(), true);
        sira.add(baslangic);

        while (!sira.isEmpty()) {
            Durak simdiki = sira.poll();

            if (simdiki.getId().equalsIgnoreCase(hedef.getId())) {
                if (!usableStopType(simdiki, tip)) return null;
                return izlenilenYol(oncekiDurak, hedef);
            }

            if (simdiki.getNextStops() != null) {
                for (NextStop ns : simdiki.getNextStops()) {
                    Durak komsu = DurakBulId(ns.getstopId(), allDuraks);
                    if (komsu != null && !ziyaretEdildi.get(komsu.getId()) && usableStopType(komsu, tip)) {
                        ziyaretEdildi.put(komsu.getId(), true);
                        oncekiDurak.put(komsu.getId(), simdiki);
                        sira.add(komsu);
                    }
                }
            }

            if (simdiki.getTransfer() != null) {
                Durak transferDurak = DurakBulId(simdiki.getTransfer().getTransferStopId(), allDuraks);
                if (transferDurak != null && !ziyaretEdildi.get(transferDurak.getId()) && usableStopType(transferDurak, tip)) {
                    ziyaretEdildi.put(transferDurak.getId(), true);
                    oncekiDurak.put(transferDurak.getId(), simdiki);
                    sira.add(transferDurak);
                }
            }
        }

        return null;
    }

    public static List<Durak> bfsOnlyBus(Durak bas, Durak hedef, List<Durak> ds) {
        return bfsYolBulmaTypeFilter(bas, hedef, ds, "bus");
    }

    public static List<Durak> bfsOnlyTram(Durak bas, Durak hedef, List<Durak> ds) {
        return bfsYolBulmaTypeFilter(bas, hedef, ds, "tram");
    }

    // ------------------------------------------------------------
    // 3) Dijkstra (En kısa süreli rota) - Genel ve Filtreli
    // ------------------------------------------------------------
    public static List<Durak> dijkstraSureRota(Durak baslangic, Durak hedef, List<Durak> allDuraks) {
        return dijkstraSureRotaTypeFilter(baslangic, hedef, allDuraks, null);
    }

    public static List<Durak> dijkstraSureOnlyBus(Durak bas, Durak hedef, List<Durak> ds) {
        return dijkstraSureRotaTypeFilter(bas, hedef, ds, "bus");
    }

    public static List<Durak> dijkstraSureOnlyTram(Durak bas, Durak hedef, List<Durak> ds) {
        return dijkstraSureRotaTypeFilter(bas, hedef, ds, "tram");
    }

    public static List<Durak> dijkstraSureRotaTypeFilter(Durak baslangic,
                                                         Durak hedef,
                                                         List<Durak> allDuraks,
                                                         String allowedType) {
        if (baslangic == null || hedef == null) return null;

        Map<String, Double> enKisaSure = new HashMap<>();
        Map<String, Durak> oncekiDurak = new HashMap<>();

        for (Durak d : allDuraks) {
            enKisaSure.put(d.getId(), Double.POSITIVE_INFINITY);
            oncekiDurak.put(d.getId(), null);
        }

        enKisaSure.put(baslangic.getId(), 0.0);
        PriorityQueue<DijkstraNode> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.sure));
        pq.add(new DijkstraNode(baslangic, 0.0));

        while (!pq.isEmpty()) {
            DijkstraNode dugum = pq.poll();
            Durak simdiki = dugum.durak;
            double simdikiSure = dugum.sure;

            if (simdikiSure > enKisaSure.get(simdiki.getId())) continue;

            if (simdiki.getId().equalsIgnoreCase(hedef.getId())) {
                if (allowedType == null || usableStopType(simdiki, allowedType)) {
                    return izlenilenYol(oncekiDurak, hedef);
                } else {
                    return null;
                }
            }

            if (simdiki.getNextStops() != null) {
                for (NextStop ns : simdiki.getNextStops()) {
                    Durak komsu = DurakBulId(ns.getstopId(), allDuraks);
                    if (komsu == null || (!usableStop(komsu))) continue;
                    if (allowedType != null && !usableStopType(komsu, allowedType)) continue;

                    double alternatif = simdikiSure + ns.getSure();
                    if (alternatif < enKisaSure.get(komsu.getId())) {
                        enKisaSure.put(komsu.getId(), alternatif);
                        oncekiDurak.put(komsu.getId(), simdiki);
                        pq.add(new DijkstraNode(komsu, alternatif));
                    }
                }
            }

            if (simdiki.getTransfer() != null) {
                Durak transferDurak = DurakBulId(simdiki.getTransfer().getTransferStopId(), allDuraks);
                if (transferDurak == null) continue;
                if (!usableStop(transferDurak)) continue;
                if (allowedType != null && !usableStopType(transferDurak, allowedType)) continue;

                double alternatif = simdikiSure + simdiki.getTransfer().getTransferSure();
                if (alternatif < enKisaSure.get(transferDurak.getId())) {
                    enKisaSure.put(transferDurak.getId(), alternatif);
                    oncekiDurak.put(transferDurak.getId(), simdiki);
                    pq.add(new DijkstraNode(transferDurak, alternatif));
                }
            }
        }

        return null;
    }

    // ------------------------------------------------------------
    // Ortak Yol Takibi (Path Reconstruction)
    // ------------------------------------------------------------
    private static List<Durak> izlenilenYol(Map<String, Durak> oncekiDurak, Durak hedef) {
        List<Durak> yol = new ArrayList<>();
        Durak simdi = hedef;
        while (simdi != null) {
            yol.add(simdi);
            simdi = oncekiDurak.get(simdi.getId());
        }
        Collections.reverse(yol);
        return yol;
    }

    // ------------------------------------------------------------
    // Yardımcı Fonksiyonlar
    // ------------------------------------------------------------
    private static Durak DurakBulId(String stopId, List<Durak> allDuraks) {
        for (Durak d : allDuraks) {
            if (d.getId().equalsIgnoreCase(stopId)) return d;
        }
        return null;
    }

    private static boolean usableStop(Durak d) {
        return true; // Şimdilik her durak kullanılabilir kabul ediliyor
    }

    private static boolean usableStopType(Durak d, String tip) {
        return d.getType() != null && d.getType().equalsIgnoreCase(tip);
    }

    private static class DijkstraNode {
        Durak durak;
        double sure;
        DijkstraNode(Durak d, double s) {
            durak = d;
            sure = s;
        }
    }
}
