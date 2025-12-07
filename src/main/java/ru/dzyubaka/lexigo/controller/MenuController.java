package ru.dzyubaka.lexigo.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceDialog;
import ru.dzyubaka.lexigo.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MenuController {
    @FXML
    private void create(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(MenuController.class.getResource("../view/edit.fxml")));
    }

    @FXML
    private void edit(ActionEvent event) {
        showChoiceDialog(name -> {
            try (var bufferedReader = Files.newBufferedReader(Path.of(name + ".csv"))) {
                var loader = new FXMLLoader(MenuController.class.getResource("../view/edit.fxml"));
                var root = loader.<Parent>load();
                var items = bufferedReader.readAllLines().stream().map(line -> {
                    var commaIndex = line.indexOf(',');
                    return new Item(line.substring(0, commaIndex), line.substring(commaIndex + 1));
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                loader.<EditController>getController().setItems(items);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void take(ActionEvent event) {
        showChoiceDialog(name -> {
            try (var bufferedReader = Files.newBufferedReader(Path.of(name + ".csv"))) {
                var items = bufferedReader.readAllLines().stream().map(line -> {
                    var commaIndex = line.indexOf(',');
                    return new Item(line.substring(0, commaIndex), line.substring(commaIndex + 1));
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                Collections.shuffle(items);
                var loader = new FXMLLoader(MenuController.class.getResource("../view/pass.fxml"));
                var root = loader.<Parent>load();
                loader.<PassController>getController().setItems(items);
                ((Node) event.getSource()).getScene().setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showChoiceDialog(Consumer<String> action) {
        try (var list = Files.list(Path.of("."))) {
            var paths = list
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".csv"))
                    .map(n -> n.substring(0, n.length() - 4))
                    .collect(Collectors.toList());
            var dialog = new ChoiceDialog<>(null, paths);
            dialog.setHeaderText("Select test");
            dialog.showAndWait().ifPresent(action);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
