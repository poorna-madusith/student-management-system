package com.example.student_managemnt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class studentms extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(studentms.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 795, 501);
        stage.setResizable(false);
        stage.setTitle("studentms");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}