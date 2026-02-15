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
import ru.dzyubaka.lexigo.Alerts;
import ru.dzyubaka.lexigo.Item;
import ru.dzyubaka.lexigo.Main;
import ru.dzyubaka.lexigo.controller.MenuController;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.random.RandomGenerator;

public class TakeTestController {

    private final RandomGenerator random = RandomGenerator.getDefault();

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

    private final Alert correctAlert = Alerts.create(Alert.AlertType.INFORMATION, "Correct!");
    private ObservableList<Item> items;

    private int currentIndex = 0;
    private int score = 0;

    {
        correctAlert.getDialogPane().setGraphic(new ImageView(Main.class.getResource("accept.png").toExternalForm()));
    }

    public void setItems(ObservableList<Item> items) {
        this.items = items;
        text.setText(items.getFirst().getRussian());
    }

    @FXML
    private void initialize() {
        int rowCount = 4;
        ObservableList<RowConstraints> rowConstraints = gridPane.getRowConstraints();
        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.setPercentHeight(100. / rowCount);

        for (int i = 0; i < rowCount; i++) {
            rowConstraints.add(rowConstraint);
        }

        int columnCount = 12;
        ColumnConstraints columnConstraint = new ColumnConstraints();
        columnConstraint.setPercentWidth(100. / columnCount);

        for (int i = 0; i < columnCount; i++) {
            gridPane.getColumnConstraints().add(columnConstraint);
        }
    }

    @FXML
    private void back(ActionEvent event) {
        if (Alerts.create(Alert.AlertType.CONFIRMATION, "Are you sure?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(MenuController.FXML);
        }
    }

    @FXML
    private void first() {
        firstButton.setDisable(true);
        Alerts.create(Alert.AlertType.INFORMATION, String.valueOf(items.get(currentIndex).getEnglish().charAt(0))).show();
        score--;
    }

    @FXML
    private void length() {
        lengthButton.setDisable(true);
        Alerts.create(Alert.AlertType.INFORMATION, String.valueOf(items.get(currentIndex).getEnglish().length())).show();
        score--;
    }

    @FXML
    private void scatter(ActionEvent event) {
        scatterButton.setDisable(true);
        textField.setEditable(false);
        nextButton.setVisible(false);
        nextButton.setManaged(false);
        gridPane.setDisable(false);
        int columnCount = gridPane.getColumnConstraints().size();
        int rowCount = gridPane.getRowConstraints().size();
        Set<Point> points = new HashSet<>();
        String english = items.get(currentIndex).getEnglish();
        english.chars().forEach(c -> {
            String text = String.valueOf((char) c);
            Button button = new Button(text);
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
                    Alerts.create(Alert.AlertType.WARNING, "Wrong letter!").show();
                }
            });
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.setFocusTraversable(false);
            Point point;
            do {
                point = new Point(random.nextInt(columnCount), random.nextInt(rowCount));
            } while (points.contains(point));
            points.add(point);
            gridPane.add(button, point.x, point.y);
        });
        score -= 2;
    }

    @FXML
    private void next(ActionEvent event) {
        if (textField.getText().equals(items.get(currentIndex).getEnglish())) {
            correctAlert.showAndWait();
            score += 5;
        } else {
            Alerts.create(Alert.AlertType.ERROR, items.get(currentIndex).getEnglish()).showAndWait();
        }
        int index = ++currentIndex;
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
