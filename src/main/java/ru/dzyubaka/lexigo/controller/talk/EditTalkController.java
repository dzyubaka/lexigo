package ru.dzyubaka.lexigo.controller.talk;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.*;
import ru.dzyubaka.lexigo.controller.MenuController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EditTalkController {
    @FXML
    private BorderPane borderPane;

    private final TextArea textArea = new TextArea() {
        @Override
        public void paste() {
            var image = Clipboard.getSystemClipboard().getImage();
            if (image != null) {
                bufferedImage = removeAlpha(image);
                imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                imageView.fitHeightProperty().bind(borderPane.heightProperty().divide(2));
            } else {
                super.paste();
            }
            dirty = true;
        }
    };

    @FXML
    private ImageView imageView;

    private boolean dirty = false;

    private Path path;

    private BufferedImage bufferedImage;

    public void loadTalk(String name) {
        path = Path.of(name + ".txt");
        try (var bufferedReader = Files.newBufferedReader(path)) {
            textArea.setText(bufferedReader.readAllAsString());
            dirty = false;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @FXML
    private void initialize() {
        textArea.textProperty().addListener(_ -> dirty = true);
        borderPane.setCenter(textArea);
    }

    @FXML
    private void back(ActionEvent event) {
        if (!dirty || new Alert(Alert.AlertType.CONFIRMATION, "Discard unsaved changes?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(MenuController.FXML);
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
            if (bufferedImage != null) {
                var fileName = path.getFileName().toString();
                var output = new File(fileName.substring(0, fileName.lastIndexOf('.')) + ".jpg");
                ImageIO.write(bufferedImage, "JPEG", output);
            }
            dirty = false;
            back(event);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static BufferedImage removeAlpha(Image image) {
        var bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_RGB);
        var graphics = bufferedImage.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.drawImage(SwingFXUtils.fromFXImage(image, null), 0, 0, null);
        return bufferedImage;
    }
}
