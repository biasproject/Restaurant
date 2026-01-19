package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.*;

public class OspatarController {

    // --- Componente FXML ---
    @FXML private Label numeOspatarLabel;
    @FXML private Label numarMasaLabel;
    @FXML private TableView<Produs> meniuTableView;
    @FXML private TableColumn<Produs, String> meniuNumeColumn;
    @FXML private TableColumn<Produs, String> meniuCategorieColumn;
    @FXML private TableColumn<Produs, Float> meniuPretColumn;
    @FXML private Spinner<Integer> cantitateSpinner;
    @FXML private TableView<ComandaItem> comandaTableView;
    @FXML private TableColumn<ComandaItem, String> comandaNumeColumn;
    @FXML private TableColumn<ComandaItem, Integer> comandaCantitateColumn;
    @FXML private TableColumn<ComandaItem, Double> comandaPretColumn;
    @FXML private Label subtotalLabel;
    @FXML private Label reduceriLabel;
    @FXML private Label totalLabel;

    // --- Date și servicii ---
    private ProdusRepository produsRepository;
    private ComandaRepository comandaRepository;
    private User ospatarCurent;
    private int masaCurenta;

    private Comanda comandaCurenta;
    private ObservableList<ComandaItem> itemsComandaAfisate;
    private OfferService offerService;

    /**
     * Aceasta NU este o metodă FXML. Va fi chemată manual din LoginController
     * pentru a trimite datele necesare (cine e logat și la ce masă).
     */
    public void initData(User ospatar, int numarMasa) {
        this.ospatarCurent = ospatar;
        this.masaCurenta = numarMasa;

        numeOspatarLabel.setText(ospatar.getUsername());
        numarMasaLabel.setText(String.valueOf(numarMasa));

        // Creăm o nouă comandă goală pentru această masă
        this.comandaCurenta = new Comanda(numarMasa, ospatarCurent);
    }

    @FXML
    public void initialize() {
        produsRepository = new ProdusRepository();
        comandaRepository = new ComandaRepository();

        // --- Setup Meniu (partea stângă) ---
        meniuNumeColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        meniuCategorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        meniuPretColumn.setCellValueFactory(new PropertyValueFactory<>("pret"));
        meniuTableView.setItems(FXCollections.observableArrayList(produsRepository.gasesteTot()));

        // --- Setup Comanda (partea dreaptă) ---
        comandaNumeColumn.setCellValueFactory(new PropertyValueFactory<>("numeProdusLaVanzare"));
        comandaCantitateColumn.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        comandaPretColumn.setCellValueFactory(new PropertyValueFactory<>("pretLaVanzare")); // Vom calcula subtotalul

        itemsComandaAfisate = FXCollections.observableArrayList();
        comandaTableView.setItems(itemsComandaAfisate);

        this.offerService = new OfferService();
    }

    @FXML
    private void handleAdaugaInCos() {
        Produs produsSelectat = meniuTableView.getSelectionModel().getSelectedItem();
        if (produsSelectat == null) {
            showAlert(Alert.AlertType.WARNING, "Nicio selecție", "Vă rugăm selectați un produs din meniu.");
            return;
        }

        int cantitate = cantitateSpinner.getValue();

        // Adăugăm un item nou în obiectul Comanda
        ComandaItem newItem = new ComandaItem(produsSelectat, cantitate, comandaCurenta);
        comandaCurenta.getItems().add(newItem);

        // Actualizăm UI-ul
        actualizeazaCosUI();
    }

    private void actualizeazaCosUI() {
        itemsComandaAfisate.setAll(comandaCurenta.getItems());

        double subtotal = 0;
        for (ComandaItem item : comandaCurenta.getItems()) {
            subtotal += item.getPretLaVanzare() * item.getCantitate();
        }

        // TODO: Aici vom chema motorul de oferte
        double valoareReduceri = offerService.aplicaOferte(comandaCurenta);

        double total = subtotal - valoareReduceri;

        subtotalLabel.setText(String.format("Subtotal: %.2f RON", subtotal));
        reduceriLabel.setText(String.format("Reduceri: -%.2f RON", valoareReduceri));
        totalLabel.setText(String.format("TOTAL: %.2f RON", total));

        comandaCurenta.setSubtotal(subtotal);
        comandaCurenta.setTotalReduceri(valoareReduceri);
        comandaCurenta.setTotalFinal(total);
    }

    @FXML
    private void handleFinalizeazaComanda() {
        if (comandaCurenta.getItems().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Comandă goală", "Nu puteți finaliza o comandă fără produse.");
            return;
        }

        comandaCurenta.setFinalizata(true);
        comandaRepository.save(comandaCurenta);

        showAlert(Alert.AlertType.INFORMATION, "Succes", "Comanda pentru masa " + masaCurenta + " a fost finalizată și salvată!");

        // TODO: Închide fereastra și revino la ecranul de selecție mese
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}