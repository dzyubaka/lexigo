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
import ru.dzyubaka.lexigo.controller.talk.TakeTalkController;
import ru.dzyubaka.lexigo.controller.test.EditTestController;
import ru.dzyubaka.lexigo.controller.test.TakeTestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MenuController {
    @FXML
    private void createTest(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(MenuController.class.getResource("test/edit-test.fxml")));
    }

    @FXML
    private void editTest(ActionEvent event) {
        showChoiceDialog(".csv", "test", name -> {
            try (var bufferedReader = Files.newBufferedReader(Path.of(name + ".csv"))) {
                var loader = new FXMLLoader(MenuController.class.getResource("test/edit-test.fxml"));
                var root = loader.<Parent>load();
                var items = bufferedReader.readAllLines().stream().map(line -> {
                    var commaIndex = line.indexOf(',');
                    return new Item(line.substring(0, commaIndex), line.substring(commaIndex + 1));
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                loader.<EditTestController>getController().setItems(items);
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
            try (var bufferedReader = Files.newBufferedReader(Path.of(name + ".txt"))) {
                var loader = new FXMLLoader(MenuController.class.getResource("talk/edit-talk.fxml"));
                var root = loader.<Parent>load();
                var text = bufferedReader.readAllAsString();
                loader.<EditTalkController>getController().setText(text);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void takeTalk(ActionEvent event) {
        showChoiceDialog(".txt", "talk", name -> {
            try (var bufferedReader = Files.newBufferedReader(Path.of(name + ".txt"))) {
                var text = bufferedReader.readAllAsString();
                var loader = new FXMLLoader(MenuController.class.getResource("talk/take-talk.fxml"));
                var root = loader.<Parent>load();
                loader.<TakeTalkController>getController().setText(text);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showChoiceDialog(String extension, String text, Consumer<String> action) {
        try (var list = Files.list(Path.of("."))) {
            var paths = list
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(extension))
                    .map(n -> n.substring(0, n.length() - 4))
                    .collect(Collectors.toList());
            if (paths.isEmpty()) {
                var alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("There are no any " + text + "s!");
                alert.show();
            } else {
                var dialog = new ChoiceDialog<>(null, paths);
                dialog.setHeaderText("Select " + text);
                dialog.showAndWait().ifPresent(action);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
