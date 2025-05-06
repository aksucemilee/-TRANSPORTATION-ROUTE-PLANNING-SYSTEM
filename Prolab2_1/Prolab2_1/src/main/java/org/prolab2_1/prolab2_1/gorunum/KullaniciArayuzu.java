package org.prolab2_1.prolab2_1.gorunum;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.prolab2_1.prolab2_1.sistem.Durak;
import org.prolab2_1.prolab2_1.sistem.SehirVerisi;
import org.prolab2_1.prolab2_1.sistem.odeme.*;
import org.prolab2_1.prolab2_1.sistem.yolcu.*;
import org.prolab2_1.prolab2_1.veri.GrafAlgoritmasi;
import org.prolab2_1.prolab2_1.veri.RotaHesaplama;
import org.prolab2_1.prolab2_1.veri.RotaSonucu;
import org.prolab2_1.prolab2_1.veri.UzaklikHesaplama;

import java.util.List;

public class KullaniciArayuzu extends StackPane {

    private TextField txtBaslangicLat;
    private TextField txtBaslangicLon;
    private TextField txtHedefLat;
    private TextField txtHedefLon;
    private ComboBox<String> cmbYolcuTipi;
    private ComboBox<String> cmbOdeme;
    private TextArea txtSonuc;

    private final SehirVerisi cityData;
    private final Harita harita;  // Harita sınıfından nesne

    public KullaniciArayuzu(SehirVerisi cityData) {
        this.cityData = cityData;

        // 1) Haritayı WebView ile göster
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        harita = new Harita(cityData, webEngine);
        harita.haritaYukle();

        webView.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.getChildren().add(webView);

        // 2) Sol tarafta form ve sonuç göstermek için panel
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setAlignment(Pos.TOP_LEFT);
        leftPanel.setPrefWidth(350);
        leftPanel.setMaxWidth(350);
        leftPanel.setFillWidth(true);
        leftPanel.setStyle("""
           -fx-background-color: rgba(255,255,255,0.7);
           -fx-background-radius: 15;
           -fx-border-radius: 15;
           -fx-font-size: 12px;
           -fx-padding: 15;
           -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0.5, 0, 2);
        """);

        Label lblBaslik = new Label("Ulaşım Rota Planlama");
        lblBaslik.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        // Yolcu Tipi
        Label lblYolcuTipi = new Label("Yolcu Tipi:");
        cmbYolcuTipi = new ComboBox<>();
        cmbYolcuTipi.getItems().addAll("Genel", "Öğrenci", "Öğretmen", "Yaşlı");
        cmbYolcuTipi.setValue("Genel");
        stilComboBox(cmbYolcuTipi);

        // Ödeme Yöntemi
        Label lblOdeme = new Label("Ödeme Yöntemi:");
        cmbOdeme = new ComboBox<>();
        cmbOdeme.getItems().addAll("KentKart", "Kredi Kartı", "Nakit");
        cmbOdeme.setValue("KentKart");
        stilComboBox(cmbOdeme);

        // Başlangıç Konumu
        Label lblBaslangic = new Label("Başlangıç Konumu (Lat, Lon):");
        txtBaslangicLat = new TextField("40.766");
        txtBaslangicLon = new TextField("29.940");
        stilTextField(txtBaslangicLat);
        stilTextField(txtBaslangicLon);

        // Hedef Konumu
        Label lblHedef = new Label("Hedef Konumu (Lat, Lon):");
        txtHedefLat = new TextField("40.760");
        txtHedefLon = new TextField("29.942");
        stilTextField(txtHedefLat);
        stilTextField(txtHedefLon);

        // Rota Hesapla butonu
        Button btnHesapla = new Button("Rota Hesapla");
        btnHesapla.setPrefWidth(120);
        btnHesapla.setStyle("""
           -fx-background-radius: 15;
           -fx-border-radius: 15;
           -fx-background-color: #E5E5E5;
           -fx-border-color: #D3D3D3;
           -fx-padding: 5;
        """);
        btnHesapla.setOnAction(e -> rotaHesapla());

        // Sonuçları gösteren TextArea
        txtSonuc = new TextArea();
        txtSonuc.setEditable(false);
        txtSonuc.setPrefRowCount(10);
        txtSonuc.setPromptText("Rota Detayları...");
        txtSonuc.setPrefWidth(Double.MAX_VALUE);
        txtSonuc.setMaxWidth(Double.MAX_VALUE);
        txtSonuc.setVisible(false);

        // Panel içeriği
        leftPanel.getChildren().addAll(
                lblBaslik,
                lblYolcuTipi, cmbYolcuTipi,
                lblOdeme, cmbOdeme,
                lblBaslangic, txtBaslangicLat, txtBaslangicLon,
                lblHedef, txtHedefLat, txtHedefLon,
                btnHesapla,
                txtSonuc
        );

        this.getChildren().add(leftPanel);
        StackPane.setAlignment(leftPanel, Pos.TOP_LEFT);
        StackPane.setMargin(leftPanel, new Insets(15, 0, 15, 20));
    }

    /**
     * Rota Hesapla butonuna tıklandığında olanlar.
     * Burada, kullanıcı→durak ve durak→hedef segmentleri için
     * 3 km kontrolü yapılarak yürüyüş veya taksi seçimi,
     * BFS/Dijkstra yol hesaplamaları ve alternatif rotalar raporlanır.
     */
    private void rotaHesapla() {
        try {
            // 1) Eski çizimleri temizle
            harita.temizleKullaniciCizimler();

            // 2) Kullanıcı girdi değerlerini al
            double basLat   = Double.parseDouble(txtBaslangicLat.getText());
            double basLon   = Double.parseDouble(txtBaslangicLon.getText());
            double hedefLat = Double.parseDouble(txtHedefLat.getText());
            double hedefLon = Double.parseDouble(txtHedefLon.getText());

            // Yolcu seçimi
            Yolcu secilenYolcu = switch (cmbYolcuTipi.getValue()) {
                case "Öğrenci"  -> new Ogrenci("Öğrenci", 20);
                case "Öğretmen" -> new Ogretmen("Öğretmen", 35);
                case "Yaşlı"    -> new Yasli("Yaşlı", 70);
                default         -> new Genel("Genel", 40);
            };

            // Ödeme seçimi
            Odeme secilenOdeme = switch (cmbOdeme.getValue()) {
                case "Kredi Kartı" -> new Kredikart();
                case "Nakit"       -> new Nakit();
                default            -> new Kentkart();
            };

            // 3) Durak listesi
            List<Durak> duraklar = cityData.getDuraklar();
            StringBuilder sb = new StringBuilder();

            if (duraklar == null || duraklar.isEmpty()) {
                sb.append("Durak bilgisi YOK! Bu yüzden mecburen taksi kullanılacak.\n\n");
                double distUserToHedef = UzaklikHesaplama.haversineUzaklik(basLat, basLon, hedefLat, hedefLon);
                double taksiSureFull = (cityData.getTaxi() != null)
                        ? cityData.getTaxi().SureHesapla(distUserToHedef) : 0.0;
                double taksiUcretFull = RotaHesaplama.taksiUcretiHesaplama(cityData, distUserToHedef, secilenYolcu, secilenOdeme);
                sb.append(String.format("Taksi ile tam mesafe: %.2f km\n", distUserToHedef));
                sb.append(String.format("⏳ Süre ~%.1f dk | 💰 Ücret : %.2f TL\n", taksiSureFull, taksiUcretFull));
                txtSonuc.setText(sb.toString());
                txtSonuc.setVisible(true);
                return;
            }

            // Haritada Başlangıç & Hedef marker çiz
            harita.cizMarker(basLat, basLon, "Başlangıç");
            harita.cizMarker(hedefLat, hedefLon, "Hedef");

            // En yakın durakları bul
            Durak basEnYakin   = RotaHesaplama.enYakinDurak(basLat, basLon, duraklar);
            Durak hedefEnYakin = RotaHesaplama.enYakinDurak(hedefLat, hedefLon, duraklar);

            // Kullanıcı→durak ve durak→hedef mesafelerini hesapla
            double distUserToDurak = UzaklikHesaplama.haversineUzaklik(basLat, basLon,
                    basEnYakin.getLat(), basEnYakin.getLon());
            double distDurakToHedef = UzaklikHesaplama.haversineUzaklik(hedefEnYakin.getLat(), hedefEnYakin.getLon(),
                    hedefLat, hedefLon);

            // 3 km kuralı
            if (distUserToDurak > 3 || distDurakToHedef > 3) {
                sb.append("Kullanıcı veya Hedef 3 km içinde durak bulamadı.\n");
                sb.append("Bu yüzden toplu taşıma kullanılamıyor, mecburen tam taksi.\n\n");
                double distUserToHedef = UzaklikHesaplama.haversineUzaklik(basLat, basLon, hedefLat, hedefLon);
                double taksiSureFull = (cityData.getTaxi() != null)
                        ? cityData.getTaxi().SureHesapla(distUserToHedef) : 0.0;
                double taksiUcretFull = RotaHesaplama.taksiUcretiHesaplama(cityData, distUserToHedef, secilenYolcu, secilenOdeme);
                sb.append(String.format("Toplam Mesafe: %.2f km\n", distUserToHedef));
                sb.append(String.format("⏳ Süre ~%.1f dk | 💰 Ücret : %.2f TL\n", taksiSureFull, taksiUcretFull));
                txtSonuc.setText(sb.toString());
                txtSonuc.setVisible(true);
                return;
            }

            // Yazı rapor başlığı
            sb.append("\n👤 Yolcu Tipi : ").append(cmbYolcuTipi.getValue()).append("\n");
            sb.append("💳 Ödeme Yöntemi : ").append(cmbOdeme.getValue()).append("\n\n");
            sb.append("┌───────────────────────────┐\n");
            sb.append("│   🚏 KULLANICI - DURAK - HEDEF ANALİZİ │\n");
            sb.append("└───────────────────────────┘\n\n");

            // (A) Kullanıcı → En Yakın Durak
            boolean userTaksi = RotaHesaplama.esikKontrol(distUserToDurak);
            harita.cizRota2Nokta(basLat, basLon, basEnYakin.getLat(), basEnYakin.getLon(), userTaksi ? "yellow" : "green");
            sb.append("1⃣ Kullanıcı Başlangıç → En Yakın Durak ( ").append(basEnYakin.getName()).append(" )\n");
            sb.append(String.format("   📐 Mesafe : %.2f km\n", distUserToDurak));

            double userToDurakSure = 0.0;
            double userToDurakUcret = 0.0;
            if (userTaksi) {
                sb.append("   🚖 Taksi (3 km'den fazla)\n");
                userToDurakUcret = RotaHesaplama.taksiUcretiHesaplama(cityData, distUserToDurak, secilenYolcu, secilenOdeme);
                if (cityData.getTaxi() != null) {
                    userToDurakSure = cityData.getTaxi().SureHesapla(distUserToDurak);
                }
                sb.append(String.format("   ⏳ Süre ~%.1f dk | 💰 Ücret : %.2f TL\n", userToDurakSure, userToDurakUcret));
            } else {
                sb.append("   🚶 Yürüyerek (3 km'den az)\n");
                userToDurakSure = RotaHesaplama.yurumeSuresi(distUserToDurak);
                sb.append(String.format("   ⏳ Süre ~%.1f dk | 💰 Ücret : 0.00 TL\n", userToDurakSure));
            }

            // (B) En Yakın Durak → Hedef
            boolean durakTaksi = RotaHesaplama.esikKontrol(distDurakToHedef);
            harita.cizRota2Nokta(hedefEnYakin.getLat(), hedefEnYakin.getLon(), hedefLat, hedefLon, durakTaksi ? "yellow" : "green");
            sb.append("\n2⃣ En Yakın Durak ( ").append(hedefEnYakin.getName()).append(" ) → Hedef\n");
            sb.append(String.format("   📐 Mesafe : %.2f km\n", distDurakToHedef));

            double durakToHedefSure = 0.0;
            double durakToHedefUcret = 0.0;
            if (durakTaksi) {
                sb.append("   🚖 Taksi (3 km'den fazla)\n");
                durakToHedefUcret = RotaHesaplama.taksiUcretiHesaplama(cityData, distDurakToHedef, secilenYolcu, secilenOdeme);
                if (cityData.getTaxi() != null) {
                    durakToHedefSure = cityData.getTaxi().SureHesapla(distDurakToHedef);
                }
                sb.append(String.format("   ⏳ Süre ~%.1f dk | 💰 Ücret : %.2f TL\n", durakToHedefSure, durakToHedefUcret));
            } else {
                sb.append("   🚶 Yürüyerek (3 km'den az)\n");
                durakToHedefSure = RotaHesaplama.yurumeSuresi(distDurakToHedef);
                sb.append(String.format("   ⏳ Süre ~%.1f dk | 💰 Ücret : 0.00 TL\n", durakToHedefSure));
            }

            // ==========================================
            // BFS (En Az Aktarmalı) Rotası
            // ==========================================
            sb.append("\n┌─────────────────┐\n");
            sb.append("│   BFS (En Az Aktarmalı)       │\n");
            sb.append("└─────────────────┘\n");

            List<Durak> bfsPath = RotaHesaplama.enAzAktarmaRota(basEnYakin, hedefEnYakin, duraklar);
            if (bfsPath == null) {
                sb.append("   BFS rota bulunamadı, otobüs/tramvay ile hedefe ulaşılamıyor.\n");
                sb.append("   Bu yüzden taksi kullanılabilir.\n\n");
            } else {
                // Artık segment bazlı çizim: her adımın rengi araç tipine göre belirlenecek
                harita.cizRotaSegmentli(bfsPath);
                RotaSonucu bfsSonuc = RotaHesaplama.hesaplaYolu(bfsPath, cityData, secilenYolcu, secilenOdeme);
                String bfsDetay = RotaHesaplama.detayliRotaMetni(bfsPath, cityData, secilenYolcu, secilenOdeme);
                sb.append(bfsDetay);

                double bfsSure = bfsSonuc.getSure();
                double bfsUcret = bfsSonuc.getUcret();
                double bfsFullSure = userToDurakSure + bfsSure + durakToHedefSure;
                double bfsFullUcret = userToDurakUcret + bfsUcret + durakToHedefUcret;
                sb.append("\n📊 TAM ROTA (Kullanıcı →Durak + BFS + Durak→Hedef):\n");
                sb.append("● ⏳ Süre: ").append(String.format("%.1f", bfsFullSure)).append(" dk\n");
                sb.append("● 💰 Ücret: ").append(String.format("%.2f", bfsFullUcret)).append(" TL\n\n");
            }

            // ==========================================
            // Dijkstra (En Kısa Süre) Rotası
            // ==========================================
            sb.append("\n┌──────────────────┐\n");
            sb.append("│   Dijkstra (En Kısa Süre)         │\n");
            sb.append("└──────────────────┘\n");

            List<Durak> dijkstraPath = RotaHesaplama.enKisaSureRota(basEnYakin, hedefEnYakin, duraklar);
            if (dijkstraPath == null) {
                sb.append("   Dijkstra rota bulunamadı.\n\n");
            } else {
                harita.cizRotaSegmentli(dijkstraPath);
                RotaSonucu dijSonuc = RotaHesaplama.hesaplaYolu(dijkstraPath, cityData, secilenYolcu, secilenOdeme);
                String dijDetay = RotaHesaplama.detayliRotaMetni(dijkstraPath, cityData, secilenYolcu, secilenOdeme);
                sb.append(dijDetay);
                double dijSure = dijSonuc.getSure();
                double dijUcret = dijSonuc.getUcret();
                double dijFullSure = userToDurakSure + dijSure + durakToHedefSure;
                double dijFullUcret = userToDurakUcret + dijUcret + durakToHedefUcret;
                sb.append("\n📊 TAM ROTA (Kullanıcı →Durak + Djikstra + Durak→Hedef):\n");
                sb.append("● ⏳ Süre: ").append(String.format("%.1f", dijFullSure)).append(" dk\n");
                sb.append("● 💰 Ücret: ").append(String.format("%.2f", dijFullUcret)).append(" TL\n\n");
            }

            sb.append("=== ALTERNATİF ROTALAR ===\n");

            // ==========================================
            // Sadece Taksi (Full) Rotası
            // ==========================================
            sb.append("\n┌────────────────┐\n");
            sb.append("│   🚖 Sadece Taksi İle         │\n");
            sb.append("└────────────────┘\n");

            double distUserToHedef = UzaklikHesaplama.haversineUzaklik(basLat, basLon, hedefLat, hedefLon);
            double taksiSureFull = (cityData.getTaxi() != null)
                    ? cityData.getTaxi().SureHesapla(distUserToHedef) : 0.0;
            double taksiUcretFull = RotaHesaplama.taksiUcretiHesaplama(cityData, distUserToHedef, secilenYolcu, secilenOdeme);
            harita.cizRota2Nokta(basLat, basLon, hedefLat, hedefLon, "yellow");
            sb.append(String.format("   📐 Mesafe : %.2f km\n", distUserToHedef));
            sb.append(String.format("   ⏳ Süre : ~%.1f dk\n", taksiSureFull));
            sb.append(String.format("   💰 Ücret : %.2f TL\n", taksiUcretFull));

            // ------------------------------------------------------------
            // Sadece Otobüs (Detaylı) Rotası
            // ------------------------------------------------------------
            sb.append("\n┌────────────────┐\n");
            sb.append("│    🚍 Sadece Otobüs ile  │\n");
            sb.append("└────────────────┘\n");
            String busMetin = RotaHesaplama.detayliSadeceOtobusRota(
                    basLat, basLon,
                    hedefLat, hedefLon,
                    duraklar,
                    cityData,
                    secilenYolcu,
                    secilenOdeme
            );
            sb.append(busMetin).append("\n");

            // ------------------------------------------------------------
            // Sadece Tramvay (Detaylı) Rotası
            // ------------------------------------------------------------
            sb.append("\n┌────────────────┐\n");
            sb.append("│    🚋 Sadece Tramvay İle  │\n");
            sb.append("└────────────────┘\n");
            String tramMetin = RotaHesaplama.detayliSadeceTramvayRota(
                    basLat, basLon,
                    hedefLat, hedefLon,
                    duraklar,
                    cityData,
                    secilenYolcu,
                    secilenOdeme
            );
            sb.append(tramMetin).append("\n");

            // Son olarak ekrana bas
            txtSonuc.setText(sb.toString());
            txtSonuc.setVisible(true);

        } catch (NumberFormatException ex) {
            txtSonuc.setText("Lütfen geçerli enlem/boylam giriniz!");
            txtSonuc.setVisible(true);
        }
    }

    // Stil metodları
    private void stilComboBox(ComboBox<String> c) {
        c.setStyle("""
           -fx-background-radius: 15;
           -fx-border-radius: 15;
           -fx-background-color: #E5E5E5;
           -fx-border-color: #D3D3D3;
           -fx-padding: 2;
        """);
        c.setPrefWidth(120);
    }

    private void stilTextField(TextField t) {
        t.setStyle("""
           -fx-background-radius: 15;
           -fx-border-radius: 15;
           -fx-background-color: #FFFFFF;
           -fx-border-color: #FFFFFF;
           -fx-padding: 3;
        """);
        t.setPrefWidth(100);
    }
}
