package com.example.spravcesouboru;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;



/**
 * Trida Main, je hlavní třída aplikace, spouští celou aplikaci
 *
 * @author  Michal Vojtuš
 * @version 1.2
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(new Locale("en"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainPane.fxml"));
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.lang");
        loader.setResources(bundle);

        Parent mainPane = loader.load();

        primaryStage.setTitle("Vojtuš Michal - A22B0350P");
        primaryStage.setScene(new Scene(mainPane, 1100, 600));
        primaryStage.show();
    }

}
