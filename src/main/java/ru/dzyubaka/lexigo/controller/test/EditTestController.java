package ru.dzyubaka.lexigo.controller.test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditTestController {
    @FXML
    private TableView<Item> tableView;

    private boolean dirty = false;

    private Path path;

    private Item removed;

    public void loadTest(String name) {
        path = Path.of(name + ".csv");
        try (var bufferedReader = Files.newBufferedReader(path)) {
            var items = bufferedReader.readAllLines().stream().map(line -> {
                var commaIndex = line.indexOf(',');
                return new Item(line.substring(0, commaIndex), line.substring(commaIndex + 1));
            }).collect(Collectors.toCollection(FXCollections::observableArrayList));
            tableView.setItems(items);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        var russianColumn = new TableColumn<Item, String>("Russian");
        russianColumn.setSortable(false);
        russianColumn.setReorderable(false);
        russianColumn.setCellValueFactory(data -> data.getValue().russianProperty());
        russianColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        russianColumn.setOnEditCommit(event -> {
            event.getRowValue().setRussian(event.getNewValue());
            dirty = true;
        });

        var englishColumn = new TableColumn<Item, String>("English");
        englishColumn.setSortable(false);
        englishColumn.setReorderable(false);
        englishColumn.setCellValueFactory(data -> data.getValue().englishProperty());
        englishColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        englishColumn.setOnEditCommit(event -> {
            event.getRowValue().setEnglish(event.getNewValue());
            dirty = true;
        });

        tableView.getColumns().setAll(russianColumn, englishColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getItems().addAll(Stream.generate(Item::new).limit(5).toList());
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        if (!dirty || new Alert(Alert.AlertType.CONFIRMATION, "Discard unsaved changes?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(EditTestController.class.getResource("../menu.fxml")));
        }
    }

    @FXML
    private void add() {
        tableView.getItems().add(new Item());
    }

    @FXML
    private void save(ActionEvent event) {
        if (path == null) {
            var dialog = new TextInputDialog();
            dialog.setHeaderText("Enter test name");
            var name = dialog.showAndWait();
            if (name.isEmpty()) return;
            path = Path.of(name.orElseThrow() + ".csv");
            if (Files.exists(path)) {
                var alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Test '" + name.orElseThrow() + "' already exists! Overwrite?");
                if (alert.showAndWait().orElseThrow() != ButtonType.OK) {
                    path = null;
                    return;
                }
            }
        }
        try (var bufferedWriter = Files.newBufferedWriter(path)) {
            for (var item : tableView.getItems()) {
                if (!item.getRussian().isBlank() && !item.getEnglish().isBlank()) {
                    bufferedWriter.append(item.getRussian()).append(',')
                            .append(item.getEnglish()).append('\n');
                }
            }
            dirty = false;
            back(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void remove() {
        var model = tableView.getSelectionModel();
        removed = model.getSelectedItem();
        tableView.getItems().remove(model.getSelectedIndex());
        dirty = true;
    }

    @FXML
    private void restore() {
        if (removed != null) {
            tableView.getItems().add(removed);
            removed = null;
        }
    }
}
