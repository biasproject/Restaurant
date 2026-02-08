package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.model.User;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import java.io.StringWriter;
import java.io.PrintWriter;

public class OspatarTableSelectController {

    @FXML private ChoiceBox<Integer> tableChoice;
    @FXML private Label infoLabel;

    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
        if (tableChoice != null) {
            tableChoice.getItems().clear();
            for (int i = 1; i <= 12; i++) tableChoice.getItems().add(i);
            tableChoice.setValue(1);
        }
        if (infoLabel != null) infoLabel.setText("Bine ai venit, " + user.getUsername());
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        Integer selected = tableChoice != null ? tableChoice.getValue() : 1;
        if (selected == null) selected = 1;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gui/OspatarView.fxml"));
            Stage stage = (Stage) tableChoice.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            OspatarController controller = loader.getController();
            controller.initData(currentUser, selected);

            stage.setTitle("Ecran Ospătar - Masa " + selected);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Eroare la deschiderea ecranului ospătar");
            alert.setHeaderText(ex.getClass().getSimpleName() + ": " + ex.getMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            TextArea ta = new TextArea(sw.toString());
            ta.setEditable(false);
            ta.setWrapText(true);
            ta.setMaxWidth(Double.MAX_VALUE);
            ta.setMaxHeight(Double.MAX_VALUE);

            alert.getDialogPane().setExpandableContent(ta);
            alert.showAndWait();
        }
    }
}
