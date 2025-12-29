module com.betapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.betapp to javafx.fxml;
    opens com.betapp.controller to javafx.fxml;
    opens com.betapp.model to javafx.base;
    opens com.betapp.dao to javafx.base;

    exports com.betapp;
}