module com.tieinternational {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens com.tieinternational to javafx.fxml;
    exports com.tieinternational;
}
