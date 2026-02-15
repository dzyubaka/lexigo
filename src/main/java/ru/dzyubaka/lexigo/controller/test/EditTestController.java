package ru.dzyubaka.lexigo.controller.test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import ru.dzyubaka.lexigo.Alerts;
import ru.dzyubaka.lexigo.Item;
import ru.dzyubaka.lexigo.controller.MenuController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
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
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            ObservableList<Item> items = bufferedReader.readAllLines().stream().map(line -> {
                int index = line.lastIndexOf(',');
                return new Item(line.substring(0, index), line.substring(index + 1));
            }).collect(Collectors.toCollection(FXCollections::observableArrayList));
            tableView.setItems(items);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @FXML
    private void initialize() {
        TableColumn<Item, String> russianColumn = new TableColumn<>("Russian");
        russianColumn.setSortable(false);
        russianColumn.setReorderable(false);
        russianColumn.setCellValueFactory(data -> data.getValue().russianProperty());
        russianColumn.setCellFactory(createCellFactory());
        russianColumn.setOnEditCommit(event -> {
            event.getRowValue().setRussian(event.getNewValue());
            dirty = true;
        });

        TableColumn<Item, String> englishColumn = new TableColumn<>("English");
        englishColumn.setSortable(false);
        englishColumn.setReorderable(false);
        englishColumn.setCellValueFactory(data -> data.getValue().englishProperty());
        englishColumn.setCellFactory(createCellFactory());
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
        if (!dirty || Alerts.create(Alert.AlertType.CONFIRMATION, "Discard unsaved changes?").showAndWait().orElseThrow() == ButtonType.OK) {
            ((Node) event.getSource()).getScene().setRoot(MenuController.FXML);
        }
    }

    @FXML
    private void add() {
        tableView.getItems().add(new Item());
    }

    @FXML
    private void save(ActionEvent event) {
        List<Item> items = tableView.getItems().stream().filter(Item::isNotBlank).toList();
        if (items.isEmpty()) {
            Alerts.create(Alert.AlertType.ERROR, "Test is blank!").show();
            return;
        }
        if (path == null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Enter test name");
            Optional<String> name = dialog.showAndWait();
            if (name.isEmpty()) return;
            path = Path.of(name.orElseThrow() + ".csv");
            if (Files.exists(path)) {
                if (Alerts.create(Alert.AlertType.CONFIRMATION, "Test \"" + name.orElseThrow() + "\" already exists! Overwrite?").showAndWait().orElseThrow() != ButtonType.OK) {
                    path = null;
                    return;
                }
            }
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            for (Item item : items) {
                bufferedWriter.append(item.getRussian()).append(',')
                        .append(item.getEnglish()).append('\n');
            }
            dirty = false;
            back(event);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @FXML
    private void remove() {
        TableView.TableViewSelectionModel<Item> model = tableView.getSelectionModel();
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

    private Callback<TableColumn<Item, String>, TableCell<Item, String>> createCellFactory() {
        return column -> {
            TextFieldTableCell<Item, String> cell = new TextFieldTableCell<>(new DefaultStringConverter());
            cell.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    int nextRow = cell.getIndex() + 1;
                    Platform.runLater(() -> {
                        tableView.getSelectionModel().select(nextRow, column);
                        tableView.edit(nextRow, column);
                    });
                }
            });
            return cell;
        };
    }
}
