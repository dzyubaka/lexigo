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
import javafx.scene.layout.BorderPane;
import ru.dzyubaka.lexigo.Alerts;
import ru.dzyubaka.lexigo.controller.MenuController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class EditTalkController {

    @FXML
    private BorderPane borderPane;

    private final TextArea textArea = new TextArea() {
        @Override
        public void paste() {
            Image image = Clipboard.getSystemClipboard().getImage();
            if (image != null) {
                setImage(removeAlpha(image));
            } else {
                super.paste();
            }
            dirty = true;
        }
    };

    @FXML
    private ImageView imageView;

    private boolean dirty;

    private Path path;

    private BufferedImage bufferedImage;

    public void loadTalk(String name) {
        path = Path.of(name + ".txt");
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            textArea.setText(bufferedReader.readAllAsString());
            File input = new File(name + ".jpg");
            if (input.exists()) {
                setImage(ImageIO.read(input));
            }
            dirty = false;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static BufferedImage removeAlpha(Image image) {
        BufferedImage bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.drawImage(SwingFXUtils.fromFXImage(image, null), 0, 0, null);
        return bufferedImage;
    }

    @FXML
    private void initialize() {
        textArea.textProperty().addListener(_ -> dirty = true);
        textArea.setWrapText(true);
        borderPane.setCenter(textArea);
    }

    @FXML
    private void back(ActionEvent event) {
        if (!dirty || Alerts.create(Alert.AlertType.CONFIRMATION, "Discard unsaved changes?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(MenuController.FXML);
        }
    }

    @FXML
    private void save(ActionEvent event) {
        if (textArea.getText().isBlank()) {
            Alerts.create(Alert.AlertType.ERROR, "Talk is blank!").show();
            return;
        }
        if (path == null || ((Node) event.getSource()).getId() != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Enter talk name");
            Optional<String> name = dialog.showAndWait();
            if (name.isEmpty()) return;
            path = Path.of(name.orElseThrow() + ".txt");
            if (Files.exists(path)) {
                if (Alerts.create(Alert.AlertType.CONFIRMATION, "Talk \"" + name.orElseThrow() + "\" already exists! Overwrite?").showAndWait().orElseThrow() != ButtonType.OK) {
                    path = null;
                    return;
                }
            }
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            bufferedWriter.write(textArea.getText());
            if (bufferedImage != null) {
                String fileName = path.getFileName().toString();
                File output = new File(fileName.substring(0, fileName.lastIndexOf('.')) + ".jpg");
                ImageIO.write(bufferedImage, "JPEG", output);
            }
            dirty = false;
            back(event);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void setImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        imageView.fitHeightProperty().bind(borderPane.heightProperty().divide(2));
    }
}
