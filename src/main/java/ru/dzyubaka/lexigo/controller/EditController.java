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
import java.util.Collections;

public class EditController {
    @FXML
    private TableView<Item> tableView;
    private boolean dirty = false;
    private Item lastRemoved;

    void setItems(ObservableList<Item> items) {
        tableView.setItems(items);
    }

    @FXML
    private void initialize() {
        var russianColumn = new TableColumn<Item, String>("Russian");
        russianColumn.setCellValueFactory(data -> data.getValue().russianProperty());
        russianColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        russianColumn.setOnEditCommit(event -> {
            event.getRowValue().setRussian(event.getNewValue());
            dirty = true;
        });

        var englishColumn = new TableColumn<Item, String>("English");
        englishColumn.setCellValueFactory(data -> data.getValue().englishProperty());
        englishColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        englishColumn.setOnEditCommit(event -> {
            event.getRowValue().setEnglish(event.getNewValue());
            dirty = true;
        });

        tableView.getColumns().setAll(russianColumn, englishColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getItems().addAll(Collections.nCopies(5, new Item("", "")));
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        if (!dirty || new Alert(Alert.AlertType.CONFIRMATION, "Discard unsaved changes?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(EditController.class.getResource("menu.fxml")));
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
                    bufferedWriter.append(item.getRussian()).append(',')
                            .append(item.getEnglish()).append('\n');
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
        var model = tableView.getSelectionModel();
        lastRemoved = model.getSelectedItem();
        tableView.getItems().remove(model.getSelectedIndex());
        dirty = true;
    }

    @FXML
    private void restore() {
        if (lastRemoved != null) {
            tableView.getItems().add(lastRemoved);
            lastRemoved = null;
        }
    }
}
