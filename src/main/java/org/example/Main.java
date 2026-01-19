package org.example;

import org.example.gui.RestaurantApp; // Importă clasa de UI
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Singurul rol al lui Main.java este acum să delege pornirea către clasa JavaFX.
        Application.launch(RestaurantApp.class, args);
    }
}