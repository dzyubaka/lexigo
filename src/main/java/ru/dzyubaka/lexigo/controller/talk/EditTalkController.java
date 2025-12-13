package ru.dzyubaka.lexigo.controller.talk;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EditTalkController {
    @FXML
    private TextArea textArea;

    private boolean dirty = false;

    private Path path;

    public void loadTalk(String name) {
        path = Path.of(name + ".txt");
        try (var bufferedReader = Files.newBufferedReader(path)) {
            textArea.setText(bufferedReader.readAllAsString());
            dirty = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        textArea.textProperty().addListener(_ -> dirty = true);
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        if (!dirty || new Alert(Alert.AlertType.CONFIRMATION, "Discard unsaved changes?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(EditTalkController.class.getResource("../menu.fxml")));
        }
    }

    @FXML
    private void save(ActionEvent event) {
        if (textArea.getText().isBlank()) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Talk is blank!");
            alert.show();
            return;
        }
        if (path == null) {
            var dialog = new TextInputDialog();
            dialog.setHeaderText("Enter talk name");
            var name = dialog.showAndWait();
            if (name.isEmpty()) return;
            path = Path.of(name.orElseThrow() + ".txt");
            if (Files.exists(path)) {
                var alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Talk '" + name.orElseThrow() + "' already exists! Overwrite?");
                if (alert.showAndWait().orElseThrow() != ButtonType.OK) {
                    path = null;
                    return;
                }
            }
        }
        try (var bufferedWriter = Files.newBufferedWriter(path)) {
            bufferedWriter.write(textArea.getText());
            dirty = false;
            back(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
