// Acesta este conținutul NOU și FINAL pentru RestaurantApp.java
package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RestaurantApp extends Application {

    public static ExecutorService executor = Executors.newFixedThreadPool(2);
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApp.class.getResource("/org/example/gui/LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 250);

        primaryStage.setTitle("Autentificare - Restaurant 'La Andrei'");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executor.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}