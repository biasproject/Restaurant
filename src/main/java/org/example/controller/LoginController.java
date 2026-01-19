package org.example.controller;

import org.example.gui.RestaurantApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.model.UserRepository;
import org.example.model.UserRole;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserRepository userRepository;

    public LoginController() {
        this.userRepository = new UserRepository();
    }

    @FXML
    private void handleLogin() throws IOException { // Adaugă 'throws IOException'
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Eroare", "Numele de utilizator și parola nu pot fi goale.");
            return;
        }

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                // Autentificare reușită!
                if (user.getRol() == UserRole.ADMIN) {
                    schimbaScena("/org/example/gui/ManagerView.fxml", "Panou Administrator");
                } else if (user.getRol() == UserRole.STAFF) {
                    // Open table selection first
                    FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApp.class.getResource("/org/example/gui/TableSelectView.fxml"));
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(new Scene(fxmlLoader.load()));

                    OspatarTableSelectController controller = fxmlLoader.getController();
                    controller.initData(user);

                    stage.setTitle("Selectează Masa");
                    stage.show();
                }
            } else {
                showAlert("Eroare", "Parolă incorectă.");
            }
        } else {
            showAlert("Eroare", "Utilizatorul '" + username + "' nu a fost găsit.");
        }
    }
    @FXML
    private void handleGuestLogin() throws IOException { // Adaugă 'throws IOException'
        schimbaScena("/org/example/gui/GuestView.fxml", "Meniu Restaurant - Mod Client");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private void schimbaScena(String fxmlPath, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApp.class.getResource(fxmlPath));
        Stage stage = (Stage) usernameField.getScene().getWindow(); // Obținem fereastra existentă!
        stage.setScene(new Scene(fxmlLoader.load())); // Doar schimbăm conținutul
        stage.setTitle(title);
        stage.show();
    }
}