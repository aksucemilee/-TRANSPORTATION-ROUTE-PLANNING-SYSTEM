package org.prolab2_1.prolab2_1.gorunum;

import javafx.scene.web.WebEngine;
import org.prolab2_1.prolab2_1.sistem.Durak;
import org.prolab2_1.prolab2_1.sistem.NextStop;
import org.prolab2_1.prolab2_1.sistem.SehirVerisi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Harita {
    private SehirVerisi cityData;
    private WebEngine webEngine;

    public Harita(SehirVerisi cityData, WebEngine webEngine) {
        this.cityData = cityData;
        this.webEngine = webEngine;
    }

    /**
     * Haritayı yükler ve JSON'daki durakları + nextStop çizimlerini otomatik ekler.
     */
    public void haritaYukle() {
        // 1) Temel HTML (Leaflet map), userLayer tanımlıyoruz
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <style>
                    #map {
                        height: 100vh;
                        width: 100vw;
                        margin: 0;
                        padding: 0;
                    }
                    html, body {
                        margin: 0;
                        padding: 0;
                        height: 100%;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                  var map = L.map('map').setView([40.7639, 29.9444], 13);
                  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                      attribution: '© OpenStreetMap contributors'
                  }).addTo(map);

                  // Kullanıcı rotalarını/işaretçilerini bu layer'a ekleyeceğiz
                  window.userLayer = L.layerGroup().addTo(map);
                </script>
            </body>
            </html>
            """;

        // 2) Haritayı yükle
        webEngine.loadContent(html);

        // 3) Harita yüklendikten sonra durakları + nextStop bağlantılarını çiz
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            switch (newState) {
                case SUCCEEDED -> duraklariCiz();
            }
        });
    }

    /**
     * JSON'daki duraklar ve nextStop bağlantılarını "kalıcı" olarak haritaya ekler.
     * Bunlar userLayer yerine doğrudan map'e ekleniyor ki sabit kalsın.
     */
    private void duraklariCiz() {
        if (cityData == null || cityData.getDuraklar() == null) return;

        List<Durak> duraklar = cityData.getDuraklar();
        Map<String, Durak> durakMap = new HashMap<>();
        for (Durak d : duraklar) {
            durakMap.put(d.getId(), d);
        }

        StringBuilder jsBuilder = new StringBuilder();
        jsBuilder.append("(function(){\n");

        // Her durak için marker + tooltip (SABİT)
        for (Durak durak : duraklar) {
            double lat = durak.getLat();
            double lon = durak.getLon();
            String name = durak.getName() != null ? durak.getName() : "Bilinmeyen Durak";
            String type = durak.getType() != null ? durak.getType() : "Bilinmeyen Tip";
            String sonDurak = durak.isSonDurak() ? "Evet" : "Hayır";

            String tooltipContent = String.format("""
                <div style="font-family: sans-serif; font-size:14px;">
                    <table style="border:1px solid #ccc; border-collapse: collapse;">
                        <tr>
                            <th colspan="2" style="border:1px solid #ccc; padding:5px; background-color:#f2f2f2;">
                                Durak Bilgileri
                            </th>
                        </tr>
                        <tr>
                            <td style="border:1px solid #ccc; padding:5px;"><b>Durak Adı</b></td>
                            <td style="border:1px solid #ccc; padding:5px;">%s</td>
                        </tr>
                        <tr>
                            <td style="border:1px solid #ccc; padding:5px;"><b>Tip</b></td>
                            <td style="border:1px solid #ccc; padding:5px;">%s</td>
                        </tr>
                        <tr>
                            <td style="border:1px solid #ccc; padding:5px;"><b>Son Durak</b></td>
                            <td style="border:1px solid #ccc; padding:5px;">%s</td>
                        </tr>
                        <tr>
                            <td style="border:1px solid #ccc; padding:5px;"><b>Koordinatlar</b></td>
                            <td style="border:1px solid #ccc; padding:5px;">(%.4f, %.4f)</td>
                        </tr>
                    </table>
                </div>
                """, name, type, sonDurak, lat, lon);

            String safeTooltip = tooltipContent
                    .replace("'", "\\'")
                    .replace("\n", " ")
                    .replace("\r", "");

            jsBuilder.append(" var marker = L.marker([").append(lat).append(",").append(lon).append("]).addTo(map);\n")
                    .append(" marker.bindTooltip('").append(safeTooltip).append("',{direction:'top'});\n");
        }

        // Sabit duraklar arası bağlantılar (gri polyline)
        for (Durak durak : duraklar) {
            if (durak.getNextStops() != null) {
                for (NextStop ns : durak.getNextStops()) {
                    Durak hedef = durakMap.get(ns.getstopId());
                    if (hedef != null) {
                        jsBuilder.append(" L.polyline([[")
                                .append(durak.getLat()).append(",").append(durak.getLon())
                                .append("],[")
                                .append(hedef.getLat()).append(",").append(hedef.getLon())
                                .append("]], {color:'gray', weight:2}).addTo(map);\n");
                    }
                }
            }
        }

        jsBuilder.append("})();\n");

        webEngine.executeScript(jsBuilder.toString());
    }

    /**
     * UserLayer temizler.
     */
    public void temizleKullaniciCizimler() {
        String script = "(function(){ userLayer.clearLayers(); })();";
        webEngine.executeScript(script);
    }

    /**
     * Kullanıcıya özel marker ekler.
     */
    public void cizMarker(double lat, double lon, String aciklama) {
        String konum = aciklama.replace("'", "\\'").replace("\n", " ").replace("\r", "");
        String script =
                "(function(){\n"
                + "  var mk = L.marker([" + lat + "," + lon + "]).addTo(userLayer);\n"
                + "  mk.bindTooltip('" + konum + "', {direction:'top'});\n"
                + "  mk.bindPopup('" + konum + " (Tıklayınca)');\n"
                + "})();\n";
        webEngine.executeScript(script);
    }

    /**
     * İki nokta arası polyline çizimi.
     */
    public void cizRota2Nokta(double lat1, double lon1, double lat2, double lon2, String renk) {
        String script =
                "(function(){\n"
                + "  var pl = L.polyline([\n"
                + "    [" + lat1 + "," + lon1 + "],\n"
                + "    [" + lat2 + "," + lon2 + "]\n"
                + "  ], {color:'" + renk + "', weight:4}).addTo(userLayer);\n"
                + "})();\n";
        webEngine.executeScript(script);
    }

    /**
     * Gelen durak listesini (rota) tek seferde çizmek yerine,
     * her segmenti araç tipine göre renklendirerek çizer.
     * Örneğin:
     * - "bus" segmenti → mavi,
     * - "tram" segmenti → turuncu,
     * - Transfer segmenti → mor.
     */
    public void cizRotaSegmentli(List<Durak> rota) {
        if (rota == null || rota.size() < 2) return;
        for (int i = 0; i < rota.size() - 1; i++) {
            Durak d1 = rota.get(i);
            Durak d2 = rota.get(i + 1);
            String renk = "gray"; // varsayılan renk

            // Eğer d1'de transfer varsa ve d1'in transferi d2'ye eşitse, transfer segmenti
            if (d1.getTransfer() != null && d1.getTransfer().getTransferStopId().equalsIgnoreCase(d2.getId())) {
                renk = "purple"; // transfer rengi
            } else if (d1.getType() != null) {
                // Arac tipine göre renk ataması
                if (d1.getType().equalsIgnoreCase("bus")) {
                    renk = "blue";
                } else if (d1.getType().equalsIgnoreCase("tram")) {
                    renk = "orange";
                } else {
                    renk = "gray";
                }
            }
            cizRota2Nokta(d1.getLat(), d1.getLon(), d2.getLat(), d2.getLon(), renk);
        }
    }

    /**
     * Gelen durak listesini tek renk ile çizer (sabit).
     */
    public void cizRota(List<Durak> rota, String renk) {
        if (rota == null || rota.size() < 2) return;
        StringBuilder koordinat = new StringBuilder("[");

        for (Durak d : rota) {
            koordinat.append("[").append(d.getLat()).append(",").append(d.getLon()).append("],");
        }
        koordinat.append("]");

        String script =
                "(function(){\n"
                + "  var pl = L.polyline(" + koordinat + ", {color:'" + renk + "', weight:4}).addTo(userLayer);\n"
                + "})();\n";
        webEngine.executeScript(script);
    }
}
