package ru.dzyubaka.lexigo.controller.talk;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import ru.dzyubaka.lexigo.controller.MenuController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GiveTalkController {
    @FXML
    private ToolBar toolBar;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextFlow textFlow;

    private Timeline timeline;

    private int prepareMillis;

    private int talkMillis;

    private int millisLeft;

    @FXML
    private void initialize() {
        progressBar.prefWidthProperty().bind(toolBar.widthProperty().subtract(60));
        var duration = new Duration(50);
        timeline = new Timeline(new KeyFrame(duration, _ -> {
            progressBar.setProgress((double) (millisLeft -= (int) (duration.toMillis())) / prepareMillis);
            if (millisLeft <= 0) {
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                timeline.stop();
                Platform.runLater(() -> {
                    var alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Timeout!");
                    alert.showAndWait();
                    millisLeft = talkMillis;
                    progressBar.setProgress(1);
                    timeline.getKeyFrames().setAll(new KeyFrame(duration, _ -> {
                        progressBar.setProgress((double) (millisLeft -= (int) (duration.toMillis())) / talkMillis);
                        if (millisLeft <= 0) {
                            timeline.stop();
                            alert.show();
                        }
                    }));
                    timeline.play();
                });
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void loadTalk(String name, char key) {
        if (key == 'O') {
            prepareMillis = 90_000;
            talkMillis = 120_000;
        } else if (key == 'E') {
            prepareMillis = talkMillis = 150_000;
        } else {
            throw new IllegalArgumentException("key = " + key);
        }
        try (var bufferedReader = Files.newBufferedReader(Path.of(name + ".txt"))) {
            var lines = bufferedReader.readAllAsString().split("\n\n");
            var text = new Text(lines[1]);
            text.setStyle("-fx-font-size: 1.25em");
            textFlow.getChildren().addAll(
                    new Text(lines[0]),
                    new Text("\n\n"),
                    text,
                    new Text("\n\n"),
                    new Text(lines[2])
            );
            millisLeft = prepareMillis;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @FXML
    private void back(ActionEvent event) {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Really exit?").showAndWait().orElseThrow() == ButtonType.OK) {
            timeline.stop();
            ((Node) event.getSource()).getScene().setRoot(MenuController.FXML);
        }
    }
}
