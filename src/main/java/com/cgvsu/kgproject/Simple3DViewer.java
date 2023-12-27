package com.cgvsu.kgproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Simple3DViewer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        AnchorPane viewport = FXMLLoader.load(Objects
                .requireNonNull(Simple3DViewer
                        .class
                        .getResource("/layouts/gui.fxml")));
        Scene scene = new Scene(viewport);
        viewport.prefWidthProperty().bind(scene.widthProperty());
        viewport.prefHeightProperty().bind(scene.heightProperty());

        InputStream iconStream = getClass().getResourceAsStream("/icons/app_icon.jpg");
        Image image = new Image(Objects.requireNonNull(iconStream));

        stage.getIcons().add(image);
        stage.setTitle("Simple3DViewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}