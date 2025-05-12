package org.prolab2_1.prolab2_1.veri;

import org.prolab2_1.prolab2_1.sistem.Durak;
import org.prolab2_1.prolab2_1.sistem.NextStop;
import org.prolab2_1.prolab2_1.sistem.SehirVerisi;

public class jsonYazKonsol {
    public static void YazSehirVerisi(SehirVerisi cityData) {
        if (cityData == null) {
            System.out.println("Şehir verisi bulunamadı.");
            return;
        }

        System.out.println("Şehir: " + cityData.getCity());
        System.out.println("Taksi Açılış Ücreti: " + cityData.getTaxi().getOpeningFee());
        System.out.println("Taksi KM Başına Ücreti: " + cityData.getTaxi().getCostPerKm());
        System.out.println("\n--- Durak Listesi ---");

        if (cityData.getDuraklar() != null) {
            for (Durak stop : cityData.getDuraklar()) {
                System.out.println("Durak ID: " + stop.getId());
                System.out.println("Durak Adı: " + stop.getName());
                System.out.println("Tip: " + stop.getType());
                System.out.println("Koordinatlar: (" + stop.getLat() + ", " + stop.getLon() + ")");
                System.out.println("Son Durak mı? " + stop.isSonDurak());

                // nextStops bilgilerini yazdırma
                if (stop.getNextStops() != null && !stop.getNextStops().isEmpty()) {
                    System.out.println("  - NextStops:");
                    for (NextStop next : stop.getNextStops()) {
                        System.out.println("     Stop ID: " + next.getstopId());
                        System.out.println("     Mesafe: " + next.getMesafe());
                        System.out.println("     Süre: " + next.getSure());
                        System.out.println("     Ücret: " + next.getUcret());
                        System.out.println("  ---");
                    }
                }

                // transfer bilgilerini yazdırma
                if (stop.getTransfer() != null) {
                    System.out.println("  - Transfer:");
                    System.out.println("     Transfer Stop ID: " + stop.getTransfer().getTransferStopId());
                    System.out.println("     Transfer Süresi: " + stop.getTransfer().getTransferSure());
                    System.out.println("     Transfer Ücreti: " + stop.getTransfer().getTransferUcret());
                }
                System.out.println("------------------------------");
            }
        }
    }
}
