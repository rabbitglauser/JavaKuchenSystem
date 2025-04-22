module com.tieinternational {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    exports com.tieinternational.app;
    opens com.tieinternational.app to javafx.fxml;
    exports com.tieinternational.controller;
    opens com.tieinternational.controller to javafx.fxml;
    exports com.tieinternational.model;
    opens com.tieinternational.model to javafx.fxml;
    exports com.tieinternational.service;
    opens com.tieinternational.service to javafx.fxml;
    exports com.tieinternational.client;
    opens com.tieinternational.client to javafx.fxml;
    exports com.tieinternational.util;
    opens com.tieinternational.util to javafx.fxml;
    exports com.tieinternational.request;
    opens com.tieinternational.request to javafx.fxml;
    exports com.tieinternational.response;
    opens com.tieinternational.response to javafx.fxml;
}
