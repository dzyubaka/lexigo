package ru.dzyubaka.lexigo.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import ru.dzyubaka.lexigo.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EditController {
    @FXML
    private TableView<Item> tableView;
    private boolean dirty = false;

    void setItems(ObservableList<Item> items) {
        tableView.setItems(items);
    }

    @FXML
    private void initialize() {
        var originalColumn = new TableColumn<Item, String>("Original");
        originalColumn.setCellValueFactory(data -> data.getValue().originalProperty());
        originalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        originalColumn.setOnEditCommit(event -> {
            event.getRowValue().setOriginal(event.getNewValue());
            dirty = true;
        });

        var translationColumn = new TableColumn<Item, String>("Translation");
        translationColumn.setCellValueFactory(data -> data.getValue().translationProperty());
        translationColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        translationColumn.setOnEditCommit(event -> {
            event.getRowValue().setTranslation(event.getNewValue());
            dirty = true;
        });

        tableView.getColumns().setAll(originalColumn, translationColumn);
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        if (!dirty || new Alert(Alert.AlertType.CONFIRMATION, "Discard unsaved changes?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(MenuController.class.getResource("/ru/dzyubaka/lexigo/view/menu.fxml")));
        }
    }

    @FXML
    private void add() {
        tableView.getItems().add(new Item("", ""));
    }

    @FXML
    private void save(ActionEvent event) {
        var dialog = new TextInputDialog();
        dialog.setHeaderText("Enter test name");
        dialog.showAndWait().ifPresent(name -> {
            var items = tableView.getItems();
            try (var bufferedWriter = Files.newBufferedWriter(Path.of(name + ".csv"))) {
                for (var item : items) {
                    bufferedWriter.append(item.getOriginal()).append(',')
                            .append(item.getTranslation()).append('\n');
                }
                dirty = false;
                back(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void remove() {
        tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
        dirty = true;
    }
}
