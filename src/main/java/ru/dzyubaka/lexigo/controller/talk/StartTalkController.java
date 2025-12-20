package ru.dzyubaka.lexigo.controller.talk;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import ru.dzyubaka.lexigo.controller.MenuController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StartTalkController {
    private final static int PREPARE_MILLIS = 90_000;

    private final static int TALK_MILLIS = 120_000;

    @FXML
    private ToolBar toolBar;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextFlow textFlow;

    private Timeline timeline;

    private int millis = PREPARE_MILLIS;

    public void loadTalk(String name) {
        try (var bufferedReader = Files.newBufferedReader(Path.of(name + ".txt"))) {
            var lines = bufferedReader.readAllAsString().split(System.lineSeparator().repeat(2));
            var text = new Text(lines[1]);
            text.setStyle("-fx-font-size: 1.5em");
            textFlow.getChildren().addAll(
                    new Text(lines[0]),
                    new Text("\n\n"),
                    text,
                    new Text("\n\n"),
                    new Text(lines[2])
            );
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
