package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class GuestController {

    @FXML private TextField searchField;
    @FXML private CheckBox vegetarianCheck;
    @FXML private ChoiceBox<String> tipChoiceBox;
    @FXML private TextField pretMinField;
    @FXML private TextField pretMaxField;
    @FXML private TableView<Produs> produseTableView;
    @FXML private TableColumn<Produs, String> numeColumn;
    @FXML private TableColumn<Produs, String> categorieColumn;
    @FXML private TableColumn<Produs, Float> pretColumn;
    @FXML private Label numeDetaliiLabel;
    @FXML private Label pretDetaliiLabel;
    @FXML private Label categorieDetaliiLabel;
    @FXML private Label specificatiiLabel;
    @FXML private Label specificatiiDetaliiLabel;

    private ProdusRepository produsRepository;
    private List<Produs> masterListProduse;
    private ObservableList<Produs> produseAfisate;


    @FXML
    public void initialize() {
        produsRepository = new ProdusRepository();
        masterListProduse = produsRepository.gasesteTot();
        produseAfisate = FXCollections.observableArrayList(masterListProduse);

        numeColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        pretColumn.setCellValueFactory(new PropertyValueFactory<>("pret"));

        produseTableView.setItems(produseAfisate);

        produseTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> afiseazaDetalii(newValue)
        );

        tipChoiceBox.setItems(FXCollections.observableArrayList("Toate", "Mâncare", "Băutură"));
        tipChoiceBox.setValue("Toate"); // Valoarea implicită
    }


    @FXML
    private void handleFiltrare() {
        List<Produs> produseFiltrate = masterListProduse.stream()
                .filter(produs -> {
                    String searchText = searchField.getText().toLowerCase();
                    if (searchText.isEmpty()) return true;
                    return produs.getNume().toLowerCase().contains(searchText);
                })
                .filter(produs -> {
                    if (!vegetarianCheck.isSelected()) return true;
                    if (produs instanceof Mancare) {
                        return ((Mancare) produs).getEsteVegetarian();
                    }
                    return false;
                })
                .filter(produs -> {
                    String tipSelectat = tipChoiceBox.getValue();
                    if (tipSelectat.equals("Toate")) return true;
                    if (tipSelectat.equals("Mâncare")) return produs instanceof Mancare;
                    if (tipSelectat.equals("Băutură")) return produs instanceof Bautura;
                    return false;
                })
                .filter(produs -> {
                    try {
                        double minPret = pretMinField.getText().isEmpty() ? 0 : Double.parseDouble(pretMinField.getText());
                        double maxPret = pretMaxField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(pretMaxField.getText());
                        return produs.getPret() >= minPret && produs.getPret() <= maxPret;
                    } catch (NumberFormatException e) {
                        return true;
                    }
                })
                .collect(Collectors.toList());

        produseAfisate.setAll(produseFiltrate);
    }


    private void afiseazaDetalii(Produs produs) {
        if (produs != null) {
            numeDetaliiLabel.setText(produs.getNume());
            pretDetaliiLabel.setText(String.format("%.2f RON", produs.getPret()));
            categorieDetaliiLabel.setText(produs.getCategorie());

            if (produs instanceof Mancare) {
                specificatiiLabel.setText("Gramaj:");
                specificatiiDetaliiLabel.setText(((Mancare) produs).getGramaj() + " g");
            } else if (produs instanceof Bautura) {
                specificatiiLabel.setText("Volum:");
                specificatiiDetaliiLabel.setText(((Bautura) produs).getVolume() + " ml");
            } else if (produs instanceof Pizza) {
                specificatiiLabel.setText("Blat:");
                specificatiiDetaliiLabel.setText(((Pizza) produs).getBlat());
            } else {
                specificatiiLabel.setText("-");
                specificatiiDetaliiLabel.setText("-");
            }
        } else {
            numeDetaliiLabel.setText("-");
            pretDetaliiLabel.setText("-");
            categorieDetaliiLabel.setText("-");
            specificatiiLabel.setText("Gramaj/Volum:");
            specificatiiDetaliiLabel.setText("-");
        }
    }
}