package ru.dzyubaka.lexigo.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import ru.dzyubaka.lexigo.Item;
import ru.dzyubaka.lexigo.controller.talk.EditTalkController;
import ru.dzyubaka.lexigo.controller.talk.StartTalkController;
import ru.dzyubaka.lexigo.controller.test.EditTestController;
import ru.dzyubaka.lexigo.controller.test.TakeTestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MenuController {
    public static final Parent FXML;

    static {
        try {
            FXML = FXMLLoader.load(MenuController.class.getResource("menu.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void createTest(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(MenuController.class.getResource("test/edit-test.fxml")));
    }

    @FXML
    private void editTest(ActionEvent event) {
        showChoiceDialog(".csv", "test", name -> {
            try {
                var loader = new FXMLLoader(MenuController.class.getResource("test/edit-test.fxml"));
                var root = loader.<Parent>load();
                loader.<EditTestController>getController().loadTest(name);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void takeTest(ActionEvent event) {
        showChoiceDialog(".csv", "test", name -> {
            try (var bufferedReader = Files.newBufferedReader(Path.of(name + ".csv"))) {
                var items = bufferedReader.readAllLines().stream().map(line -> {
                    var commaIndex = line.indexOf(',');
                    return new Item(line.substring(0, commaIndex), line.substring(commaIndex + 1));
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                Collections.shuffle(items);
                var loader = new FXMLLoader(MenuController.class.getResource("test/take-test.fxml"));
                var root = loader.<Parent>load();
                loader.<TakeTestController>getController().setItems(items);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void createTalk(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(MenuController.class.getResource("talk/edit-talk.fxml")));
    }

    @FXML
    private void editTalk(ActionEvent event) {
        showChoiceDialog(".txt", "talk", name -> {
            try {
                var loader = new FXMLLoader(MenuController.class.getResource("talk/edit-talk.fxml"));
                var root = loader.<Parent>load();
                loader.<EditTalkController>getController().loadTalk(name);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void startTalk(ActionEvent event) {
        showChoiceDialog(".txt", "talk", name -> {
            try {
                var loader = new FXMLLoader(MenuController.class.getResource("talk/start-talk.fxml"));
                var root = loader.<Parent>load();
                loader.<StartTalkController>getController().loadTalk(name);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showChoiceDialog(String extension, String text, Consumer<String> action) {
        try (var paths = Files.list(Path.of("."))
                .filter(p -> p.getFileName().toString().endsWith(extension))
                .sorted(Comparator.comparing((Path path) -> {
                    try {
                        return Files.readAttributes(path, BasicFileAttributes.class).creationTime();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).reversed())) {
            var names = paths.map(p -> {
                var name = p.getFileName().toString();
                return name.substring(0, name.lastIndexOf('.'));
            }).toList();
            if (names.isEmpty()) {
                var alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("There are no %ss!".formatted(text));
                alert.show();
            } else {
                var dialog = new ChoiceDialog<>(null, names);
                dialog.setHeaderText("Select a " + text);
                dialog.showAndWait().ifPresent(action);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
