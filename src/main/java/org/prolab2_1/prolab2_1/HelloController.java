package org.prolab2_1.prolab2_1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText; // FXML'deki fx:id="welcomeText"

    @FXML
    private Label cityLabel;   // FXML'deki fx:id="cityLabel"

    // Bu metot, FXML yüklendikten hemen sonra çağrılır.
    @FXML
    public void initialize() {
        // Ekran yüklendiğinde varsayılan metinleri ayarlayabilirsiniz
        welcomeText.setText("Merhaba, JSON Test Uygulaması!");
        cityLabel.setText("Şehir Henüz Bilinmiyor");
    }

    // Düğme tıklandığında çalışacak örnek metot
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Butona tıklandı!");
    }

    // City verisini dışarıdan (HelloApplication) set edebilmek için bir metot
    public void setCityName(String cityName) {
        cityLabel.setText("Şehir: " + cityName);
    }
}