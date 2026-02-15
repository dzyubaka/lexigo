package ru.dzyubaka.lexigo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import ru.dzyubaka.lexigo.Alerts;
import ru.dzyubaka.lexigo.Item;
import ru.dzyubaka.lexigo.controller.talk.EditTalkController;
import ru.dzyubaka.lexigo.controller.talk.GiveTalkController;
import ru.dzyubaka.lexigo.controller.test.EditTestController;
import ru.dzyubaka.lexigo.controller.test.TakeTestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MenuController {

    public static final Parent FXML;

    static {
        try {
            FXML = FXMLLoader.load(MenuController.class.getResource("menu.fxml"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
                FXMLLoader loader = new FXMLLoader(MenuController.class.getResource("test/edit-test.fxml"));
                Parent root = loader.load();
                loader.<EditTestController>getController().loadTest(name);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, false);
    }

    @FXML
    private void takeTest(ActionEvent event) {
        showChoiceDialog(".csv", "test", name -> {
            try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(name + ".csv"))) {
                ObservableList<Item> items = bufferedReader.readAllLines().stream().map(line -> {
                    int index = line.lastIndexOf(',');
                    return new Item(line.substring(0, index), line.substring(index + 1));
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                Collections.shuffle(items);
                FXMLLoader loader = new FXMLLoader(MenuController.class.getResource("test/take-test.fxml"));
                Parent root = loader.load();
                loader.<TakeTestController>getController().setItems(items);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, false);
    }

    @FXML
    private void createTalk(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(MenuController.class.getResource("talk/edit-talk.fxml")));
    }

    @FXML
    private void editTalk(ActionEvent event) {
        Node source = (Node) event.getSource();
        showChoiceDialog(".txt", "talk", name -> {
            try {
                FXMLLoader loader = new FXMLLoader(MenuController.class.getResource("talk/edit-talk.fxml"));
                Parent root = loader.load();
                loader.<EditTalkController>getController().loadTalk(name);
                source.getScene().setRoot(root);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, source.getId().endsWith("EGE"));
    }

    @FXML
    private void giveTalk(ActionEvent event) {
        Node source = (Node) event.getSource();
        showChoiceDialog(".txt", "talk", name -> {
            try {
                FXMLLoader loader = new FXMLLoader(MenuController.class.getResource("talk/give-talk.fxml"));
                Parent root = loader.load();
                loader.<GiveTalkController>getController().loadTalk(name, Files.exists(Path.of(name + ".jpg")));
                source.getScene().setRoot(root);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, source.getId().endsWith("EGE"));
    }

    private void showChoiceDialog(String extension, String text, Consumer<String> action, boolean ege) {
        try (Stream<Path> paths = Files.list(Path.of("."))
                .filter(p -> p.getFileName().toString().endsWith(extension) && ege ^ Files.notExists(Path.of(p.getFileName().toString().replace("txt", "jpg"))))
                .sorted(Comparator.comparing((Path path) -> {
                    try {
                        return Files.readAttributes(path, BasicFileAttributes.class).creationTime();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).reversed())) {
            List<String> names = paths.map(p -> {
                String name = p.getFileName().toString();
                return name.substring(0, name.lastIndexOf('.'));
            }).toList();
            if (names.isEmpty()) {
                Alerts.create(Alert.AlertType.INFORMATION, "There are no %ss!".formatted(text)).show();
            } else {
                ChoiceDialog<String> dialog = new ChoiceDialog<>();
                dialog.getItems().addAll(names);
                dialog.setHeaderText("Select a " + text);
                dialog.showAndWait().ifPresent(action);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
