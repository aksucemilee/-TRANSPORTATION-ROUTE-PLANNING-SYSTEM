package org.prolab2_1.prolab2_1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.prolab2_1.prolab2_1.sistem.SehirVerisi;
import org.prolab2_1.prolab2_1.veri.JsonVeriOkuma;
import org.prolab2_1.prolab2_1.gorunum.KullaniciArayuzu;
import org.prolab2_1.prolab2_1.veri.jsonYazKonsol;

public class Main extends Application {
    private SehirVerisi cityData; // JSON'dan gelen veriyi saklayacağız

    @Override
    public void init() throws Exception {
        super.init();
        try {
            // 1) JsonDataLoader'ı oluştur
            JsonVeriOkuma loader = new JsonVeriOkuma();
            // 2) veri_seti.json dosyasından CityData nesnesini çek
            cityData = loader.sehirVerisiOku("C:/Users/cemile/OneDrive/Masaüstü/veri_seti.json");
            // Konsol çıktısı için ayrı sınıfı kullan
            jsonYazKonsol.YazSehirVerisi(cityData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void start(Stage primaryStage) {
        if(cityData == null) {
            System.err.println("Şehir verisi yüklenemedi!!");
        }
        // "KullaniciArayuzu" nesnesini oluşturup cityData'yı veriyoruz
        KullaniciArayuzu root = new KullaniciArayuzu(cityData);

        // Sahne oluştur
        Scene scene = new Scene(root, 1200, 650);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}