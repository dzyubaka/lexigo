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

public class TakeController {
    @FXML
    private Text text;

    @FXML
    private TextField textField;

    private final Alert correctAlert = new Alert(Alert.AlertType.INFORMATION);
    private ObservableList<Item> items;

    private int currentIndex = 0;
    private int score = 0;

    {
        correctAlert.setHeaderText("Correct!");
        correctAlert.getDialogPane().setGraphic(new ImageView(TakeController.class.getResource("../accept.png").toExternalForm()));
    }

    void setItems(ObservableList<Item> items) {
        this.items = items;
        text.setText(items.getFirst().getRussian());
    }

    @FXML
    private void next(ActionEvent event) throws IOException {
        if (textField.getText().equals(items.get(currentIndex).getEnglish())) {
            correctAlert.showAndWait();
            score += 5;
        } else {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(items.get(currentIndex).getEnglish());
            alert.showAndWait();
        }
        var index = ++currentIndex;
        if (index < items.size()) {
            text.setText(items.get(index).getRussian());
            textField.setText("");
        } else {
            Alert alert;
            if (score == items.size() * 5) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().setGraphic(correctAlert.getGraphic());
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
            }
            alert.setHeaderText("Score: %d/%d".formatted(score, items.size() * 5));
            alert.showAndWait();
            ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(TakeController.class.getResource("../view/menu.fxml")));
        }
    }
}
