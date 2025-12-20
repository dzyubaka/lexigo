module ru.dzyubaka.lexigo {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    opens ru.dzyubaka.lexigo.controller to javafx.fxml;
    opens ru.dzyubaka.lexigo.controller.test to javafx.fxml;
    opens ru.dzyubaka.lexigo.controller.talk to javafx.fxml;
    exports ru.dzyubaka.lexigo;
}
