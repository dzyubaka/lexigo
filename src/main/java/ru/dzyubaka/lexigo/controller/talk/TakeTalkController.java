package ru.dzyubaka.lexigo.controller.talk;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToolBar;
import javafx.util.Duration;

import java.io.IOException;

public class TakeTalkController {
    private final static int PREPARE_MILLIS = 90_000;

    private final static int TALK_MILLIS = 120_000;

    @FXML
    private ToolBar toolBar;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label label;

    private Timeline timeline;

    private int millis = PREPARE_MILLIS;

    public void setText(String text) {
        label.setText(text);
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        timeline.stop();
        ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(TakeTalkController.class.getResource("../menu.fxml")));
    }

    @FXML
    private void initialize() {
        progressBar.prefWidthProperty().bind(toolBar.widthProperty().subtract(60));
        var duration = new Duration(50);
        timeline = new Timeline(new KeyFrame(duration, _ -> {
            progressBar.setProgress((double) (millis -= (int) (duration.toMillis())) / PREPARE_MILLIS);
            if (millis <= 0) {
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                timeline.stop();
                Platform.runLater(() -> {
                    var alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Timeout!");
                    alert.showAndWait();
                    millis = TALK_MILLIS;
                    progressBar.setProgress(1);
                    timeline.getKeyFrames().setAll(new KeyFrame(duration, _ -> {
                        progressBar.setProgress((double) (millis -= (int) (duration.toMillis())) / TALK_MILLIS);
                        if (millis <= 0) {
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
}
