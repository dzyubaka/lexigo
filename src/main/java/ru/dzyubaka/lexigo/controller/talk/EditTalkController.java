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

    @FXML
    private void initialize() {
        textArea.textProperty().addListener(_ -> dirty = true);
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        if (!dirty || new Alert(Alert.AlertType.CONFIRMATION, "Discard unsaved changes?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(EditTalkController.class.getResource("../menu.fxml")));
        }
    }

    @FXML
    private void save(ActionEvent event) {
        var dialog = new TextInputDialog();
        dialog.setHeaderText("Enter talk name");
        dialog.showAndWait().ifPresent(name -> {
            try (var bufferedWriter = Files.newBufferedWriter(Path.of(name + ".txt"))) {
                bufferedWriter.write(textArea.getText());
                dirty = false;
                back(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
