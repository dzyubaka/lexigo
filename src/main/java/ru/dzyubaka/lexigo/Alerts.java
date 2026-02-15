package ru.dzyubaka.lexigo;

import javafx.scene.control.Alert;

public class Alerts {
    public static Alert create(Alert.AlertType alertType, String headerText) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        return alert;
    }
}
