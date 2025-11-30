package ru.dzyubaka.lexigo.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import ru.dzyubaka.lexigo.Item;
import ru.dzyubaka.lexigo.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuController {
    @FXML
    private void create(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(MenuController.class.getResource("/ru/dzyubaka/lexigo/view/edit.fxml")));
    }

    @FXML
    private void edit(ActionEvent event) {
        var scene = ((Node) event.getSource()).getScene();
        Optional.ofNullable(Main.fileChooser.showOpenDialog(scene.getWindow())).ifPresent(file -> {
            try (var bufferedReader = Files.newBufferedReader(file.toPath())) {
                var loader = new FXMLLoader(MenuController.class.getResource("/ru/dzyubaka/lexigo/view/edit.fxml"));
                var root = loader.<Parent>load();
                var items = bufferedReader.readAllLines().stream().map(line -> {
                    var commaIndex = line.indexOf(',');
                    return new Item(line.substring(0, commaIndex), line.substring(commaIndex + 1));
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                loader.<EditController>getController().setItems(items);
                scene.setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void take(ActionEvent event) {
        var scene = ((Node) event.getSource()).getScene();
        Optional.ofNullable(Main.fileChooser.showOpenDialog(scene.getWindow())).ifPresent(file -> {
            try (var bufferedReader = Files.newBufferedReader(file.toPath())) {
                var items = bufferedReader.readAllLines().stream().map(line -> {
                    var commaIndex = line.indexOf(',');
                    return new Item(line.substring(0, commaIndex), line.substring(commaIndex + 1));
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                Collections.shuffle(items);
                var loader = new FXMLLoader(MenuController.class.getResource("/ru/dzyubaka/lexigo/view/pass.fxml"));
                var root = loader.<Parent>load();
                loader.<PassController>getController().setItems(items);
                scene.setRoot(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
