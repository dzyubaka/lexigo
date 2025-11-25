package ru.dzyubaka.lexigo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main extends Application {
    private final FileChooser fileChooser = new FileChooser();
    private Stage stage;
    private Scene scene;
    private VBox mainMenu;

    {
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        var createButton = new Button("Create");
        createButton.setOnAction(_ -> open(FXCollections.observableArrayList()));
        var openButton = new Button("Open");
        openButton.setOnAction(_ -> Optional.ofNullable(fileChooser.showOpenDialog(primaryStage)).ifPresent(file -> open(load(file.toPath()))));
        var takeButton = new Button("Take");
        takeButton.setOnAction(_ -> take());
        mainMenu = new VBox(new Region(), createButton, new Region(), openButton, new Region(), takeButton, new Region());
        for (var child : mainMenu.getChildren()) {
            VBox.setVgrow(child, Priority.ALWAYS);
        }
        mainMenu.setAlignment(Pos.CENTER);
        scene = new Scene(mainMenu, 640, 360);
        scene.getStylesheets().add("/styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("LexiGo! v0.3");
        primaryStage.show();
    }

    private void open(ObservableList<Item> items) {
        var tableView = new TableView<>(items);
        tableView.setEditable(true);

        var originalColumn = new TableColumn<Item, String>("Original");
        originalColumn.setCellValueFactory(data -> data.getValue().originalProperty());
        originalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        originalColumn.setOnEditCommit(event -> event.getRowValue().setOriginal(event.getNewValue()));

        var translationColumn = new TableColumn<Item, String>("Translation");
        translationColumn.setCellValueFactory(data -> data.getValue().translationProperty());
        translationColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        translationColumn.setOnEditCommit(event -> event.getRowValue().setTranslation(event.getNewValue()));

        tableView.getColumns().setAll(originalColumn, translationColumn);
        var root = new BorderPane(tableView);
        var backButton = new Button("Back");
        backButton.setOnAction(_ -> scene.setRoot(mainMenu));
        var addButton = new Button("Add");
        addButton.setOnAction(_ -> tableView.getItems().add(new Item()));
        var saveButton = new Button("Save");
        saveButton.setOnAction(_ -> Optional.ofNullable(fileChooser.showSaveDialog(stage)).ifPresent(file -> save(file.toPath(), tableView.getItems())));
        root.setTop(new ToolBar(backButton, addButton, saveButton));
        scene.setRoot(root);
    }

    private void take() {
        Optional.ofNullable(fileChooser.showOpenDialog(stage)).ifPresent(file -> {
            var items = load(file.toPath());
            Collections.shuffle(items);
            var currentIndex = new AtomicInteger();
            var correctAnswers = new AtomicInteger();
            var text = new Text(items.get(currentIndex.get()).getOriginal());
            var textField = new TextField();
            textField.setAlignment(Pos.CENTER);
            var correctAlert = new Alert(Alert.AlertType.INFORMATION);
            correctAlert.setHeaderText("Correct!");
            correctAlert.getDialogPane().setGraphic(new ImageView(getClass().getResource("/accept.png").toExternalForm()));
            textField.setOnAction(_ -> {
                if (textField.getText().equals(items.get(currentIndex.get()).getTranslation())) {
                    correctAlert.showAndWait();
                    correctAnswers.getAndIncrement();
                } else {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(items.get(currentIndex.get()).getTranslation());
                    alert.showAndWait();
                }
                var index = currentIndex.incrementAndGet();
                if (index < items.size()) {
                    text.setText(items.get(index).getOriginal());
                    textField.setText("");
                } else {
                    var alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Correct answers: %d/%d".formatted(correctAnswers.get(), items.size()));
                    alert.showAndWait();
                    scene.setRoot(mainMenu);
                }
            });
            var nextButton = new Button("Next");
            nextButton.setOnAction(textField.getOnAction());
            var root = new VBox(new Region(), text, new Region(), textField, new Region(), nextButton, new Region());
            root.setAlignment(Pos.CENTER);
            for (var child : root.getChildren()) {
                VBox.setVgrow(child, Priority.ALWAYS);
            }
            scene.setRoot(root);
        });
    }

    private void save(Path path, ObservableList<Item> items) {
        try (var bufferedWriter = Files.newBufferedWriter(path)) {
            for (var item : items) {
                bufferedWriter.append(item.getOriginal()).append(',')
                        .append(item.getTranslation()).append('\n');
            }
            scene.setRoot(mainMenu);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ObservableList<Item> load(Path path) {
        try (var bufferedReader = Files.newBufferedReader(path)) {
            return bufferedReader.readAllLines().stream().map(line -> {
                var commaIndex = line.indexOf(',');
                return new Item(line.substring(0, commaIndex), line.substring(commaIndex + 1));
            }).collect(Collectors.toCollection(FXCollections::observableArrayList));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
