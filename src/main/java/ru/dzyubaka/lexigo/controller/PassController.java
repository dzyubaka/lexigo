package ru.dzyubaka.lexigo.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import ru.dzyubaka.lexigo.Item;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class PassController {
    @FXML
    private Text text;

    @FXML
    private TextField textField;

    private ObservableList<Item> items;
    private final AtomicInteger currentIndex = new AtomicInteger();
    private final AtomicInteger correctAnswers = new AtomicInteger();
    private final Alert correctAlert = new Alert(Alert.AlertType.INFORMATION);

    {
        correctAlert.setHeaderText("Correct!");
        correctAlert.getDialogPane().setGraphic(new ImageView(PassController.class.getResource("/ru/dzyubaka/lexigo/accept.png").toExternalForm()));
    }

    void setItems(ObservableList<Item> items) {
        this.items = items;
        text.setText(items.getFirst().getOriginal());
    }

    @FXML
    private void next(ActionEvent event) throws IOException {
        if (textField.getText().equals(items.get(currentIndex.get()).getTranslation())) {
            correctAlert.showAndWait();
            correctAnswers.getAndIncrement();
        } else {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(items.get(currentIndex.get()).getTranslation());
            alert.showAndWait();
        }
        var index = currentIndex.incrementAndGet();
        if (index < items.size()) {
            text.setText(items.get(index).getOriginal());
            textField.setText("");
        } else {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Correct answers: %d/%d".formatted(correctAnswers.get(), items.size()));
            alert.showAndWait();
            ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(PassController.class.getResource("/ru/dzyubaka/lexigo/view/menu.fxml")));
        }
    }
}
