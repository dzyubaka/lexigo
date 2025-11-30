package ru.dzyubaka.lexigo.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import ru.dzyubaka.lexigo.Item;
import ru.dzyubaka.lexigo.Main;

import java.io.IOException;
import java.nio.file.Files;

public class EditController {
    @FXML
    private TableView<Item> tableView;

    void setItems(ObservableList<Item> items) {
        tableView.setItems(items);
    }

    @FXML
    private void initialize() {
        var originalColumn = new TableColumn<Item, String>("Original");
        originalColumn.setCellValueFactory(data -> data.getValue().originalProperty());
        originalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        originalColumn.setOnEditCommit(event -> event.getRowValue().setOriginal(event.getNewValue()));

        var translationColumn = new TableColumn<Item, String>("Translation");
        translationColumn.setCellValueFactory(data -> data.getValue().translationProperty());
        translationColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        translationColumn.setOnEditCommit(event -> event.getRowValue().setTranslation(event.getNewValue()));

        tableView.getColumns().setAll(originalColumn, translationColumn);
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().setRoot(FXMLLoader.load(MenuController.class.getResource("/ru/dzyubaka/lexigo/view/menu.fxml")));
    }

    @FXML
    private void add() {
        tableView.getItems().add(new Item("", ""));
    }

    @FXML
    private void save(ActionEvent event) {
        var file = Main.fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            var items = tableView.getItems();
            try (var bufferedWriter = Files.newBufferedWriter(file.toPath())) {
                for (var item : items) {
                    bufferedWriter.append(item.getOriginal()).append(',')
                            .append(item.getTranslation()).append('\n');
                }
                back(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
