module org.prolab2_1.prolab2_1 {
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires javafx.web;

    // Jackson'ın erişebileceği paketleri açıyoruz
    opens org.prolab2_1.prolab2_1.sistem to com.fasterxml.jackson.databind;
    opens org.prolab2_1.prolab2_1.sistem.arac to com.fasterxml.jackson.databind;

    // FXML erişimleri
    opens org.prolab2_1.prolab2_1 to javafx.fxml;

    // Export edilen paketler
    exports org.prolab2_1.prolab2_1;
    exports org.prolab2_1.prolab2_1.sistem;
    exports org.prolab2_1.prolab2_1.sistem.arac;
}
