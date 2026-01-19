// Acesta este conținutul NOU și FINAL pentru RestaurantApp.java
package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Aceasta este clasa principală care pornește întreaga aplicație JavaFX.
 * Singurul ei rol este să încarce și să afișeze fereastra de login.
 */
public class RestaurantApp extends Application {

    public static ExecutorService executor = Executors.newFixedThreadPool(2);
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApp.class.getResource("/org/example/gui/LoginView.fxml"));
        // Creăm "scena" și setăm dimensiunile ferestrei
        Scene scene = new Scene(fxmlLoader.load(), 350, 250);

        primaryStage.setTitle("Autentificare - Restaurant 'La Andrei'");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executor.shutdownNow(); // Oprim toate firele de execuție
    }

    public static void main(String[] args) {
        // Această metodă lansează aplicația JavaFX
        launch(args);
    }
}