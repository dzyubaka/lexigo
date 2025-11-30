module ru.dzyubaka.lexigo {
    requires javafx.controls;
    requires javafx.fxml;
    opens ru.dzyubaka.lexigo.controller to javafx.fxml;
    exports ru.dzyubaka.lexigo;
}
