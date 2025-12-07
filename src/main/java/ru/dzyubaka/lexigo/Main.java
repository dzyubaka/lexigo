package ru.dzyubaka.lexigo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
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
