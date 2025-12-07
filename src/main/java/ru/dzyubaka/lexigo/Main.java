package ru.dzyubaka.lexigo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    public static final FileChooser fileChooser = new FileChooser();

    public static void main(String[] args) {
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var scene = new Scene(FXMLLoader.load(Main.class.getResource("/ru/dzyubaka/lexigo/view/menu.fxml")), 640, 360);
        scene.getStylesheets().add("/ru/dzyubaka/lexigo/styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("LexiGo! v0.3.0");
        primaryStage.show();
    }
}
