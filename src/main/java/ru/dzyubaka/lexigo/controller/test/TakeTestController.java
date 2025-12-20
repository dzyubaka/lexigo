package ru.dzyubaka.lexigo.controller.test;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.util.Pair;
import ru.dzyubaka.lexigo.Item;
import ru.dzyubaka.lexigo.Main;
import ru.dzyubaka.lexigo.controller.MenuController;

import java.util.HashSet;
import java.util.Random;

public class TakeTestController {
    private final Random random = new Random();

    @FXML
    private Button firstButton;

    @FXML
    private Button lengthButton;

    @FXML
    private Button scatterButton;

    @FXML
    private Text text;

    @FXML
    private TextField textField;

    @FXML
    private Button nextButton;

    @FXML
    private GridPane gridPane;

    private final Alert correctAlert = new Alert(Alert.AlertType.INFORMATION);
    private ObservableList<Item> items;

    private int currentIndex = 0;
    private int score = 0;

    {
        correctAlert.setHeaderText("Correct!");
        correctAlert.getDialogPane().setGraphic(new ImageView(Main.class.getResource("accept.png").toExternalForm()));
    }

    public void setItems(ObservableList<Item> items) {
        this.items = items;
        text.setText(items.getFirst().getRussian());
    }

    @FXML
    private void initialize() {
        var rowCount = 4;
        var rowConstraints = gridPane.getRowConstraints();
        var rowConstraint = new RowConstraints();
        rowConstraint.setPercentHeight(100. / rowCount);

        for (int i = 0; i < rowCount; i++) {
            rowConstraints.add(rowConstraint);
        }

        var columnCount = 12;
        var columnConstraints = gridPane.getColumnConstraints();
        var columnConstraint = new ColumnConstraints();
        columnConstraint.setPercentWidth(100. / columnCount);

        for (int i = 0; i < columnCount; i++) {
            columnConstraints.add(columnConstraint);
        }
    }

    @FXML
    private void back(ActionEvent event) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure?");
        if (alert.showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(MenuController.FXML);
        }
    }

    @FXML
    private void first() {
        firstButton.setDisable(true);
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(String.valueOf(items.get(currentIndex).getEnglish().charAt(0)));
        alert.show();
        score--;
    }

    @FXML
    private void length() {
        lengthButton.setDisable(true);
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(String.valueOf(items.get(currentIndex).getEnglish().length()));
        alert.show();
        score--;
    }

    @FXML
    private void scatter(ActionEvent event) {
        scatterButton.setDisable(true);
        textField.setEditable(false);
        nextButton.setVisible(false);
        nextButton.setManaged(false);
        gridPane.setDisable(false);
        var columnCount = gridPane.getColumnConstraints().size();
        var rowCount = gridPane.getRowConstraints().size();
        var points = new HashSet<Pair<Integer, Integer>>();
        var english = items.get(currentIndex).getEnglish();
        english.chars().forEach(c -> {
            var text = String.valueOf((char) c);
            var button = new Button(text);
            button.setOnAction(_ -> {
                if (english.startsWith(textField.getText() + text)) {
                    textField.appendText(text);
                    gridPane.getChildren().remove(button);
                    if (english.length() == textField.getLength()) {
                        nextButton.setVisible(true);
                        nextButton.setManaged(true);
                        next(event);
                        scatterButton.setDisable(false);
                        textField.setEditable(false);
                    }
                } else {
                    var alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Wrong letter!");
                    alert.show();
                }
            });
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.setFocusTraversable(false);
            Pair<Integer, Integer> pair;
            do {
                pair = new Pair<>(random.nextInt(columnCount), random.nextInt(rowCount));
            } while (points.contains(pair));
            points.add(pair);
            gridPane.add(button, pair.getKey(), pair.getValue());
        });
        score -= 2;
    }

    @FXML
    private void next(ActionEvent event) {
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
            firstButton.setDisable(false);
            lengthButton.setDisable(false);
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
            ((Node) event.getSource()).getScene().setRoot(MenuController.FXML);
        }
    }
}
