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

    // --- Componente FXML (legătura cu design-ul) ---
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

    // --- Date ---
    private ProdusRepository produsRepository;
    private List<Produs> masterListProduse; // Lista originală, nemodificată, din DB
    private ObservableList<Produs> produseAfisate; // Lista care se afișează în tabel


    @FXML
    public void initialize() {
        // 1. Initializăm repository-ul și încărcăm datele
        produsRepository = new ProdusRepository();
        masterListProduse = produsRepository.gasesteTot();
        produseAfisate = FXCollections.observableArrayList(masterListProduse);

        // 2. Configurăm coloanele tabelului să știe ce date să afișeze din obiectul Produs
        numeColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        pretColumn.setCellValueFactory(new PropertyValueFactory<>("pret"));

        // 3. Setăm datele în tabel
        produseTableView.setItems(produseAfisate);

        // 4. Adăugăm un "ascultător" care ne anunță când utilizatorul selectează un rând
        produseTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> afiseazaDetalii(newValue)
        );

        // 5. Configurăm opțiunile pentru filtrul de tip
        tipChoiceBox.setItems(FXCollections.observableArrayList("Toate", "Mâncare", "Băutură"));
        tipChoiceBox.setValue("Toate"); // Valoarea implicită
    }


    @FXML
    private void handleFiltrare() {
        // Pornim cu lista completă de produse
        List<Produs> produseFiltrate = masterListProduse.stream()
                // Filtrul #1: Căutare după text
                .filter(produs -> {
                    String searchText = searchField.getText().toLowerCase();
                    if (searchText.isEmpty()) return true; // Dacă nu e text, trec toate
                    return produs.getNume().toLowerCase().contains(searchText);
                })
                // Filtrul #2: Doar vegetariene
                .filter(produs -> {
                    if (!vegetarianCheck.isSelected()) return true; // Dacă nu e bifat, trec toate
                    if (produs instanceof Mancare) {
                        return ((Mancare) produs).getEsteVegetarian();
                    }
                    return false; // O băutură nu e vegetariană în contextul ăsta
                })
                // Filtrul #3: Tipul produsului
                .filter(produs -> {
                    String tipSelectat = tipChoiceBox.getValue();
                    if (tipSelectat.equals("Toate")) return true;
                    if (tipSelectat.equals("Mâncare")) return produs instanceof Mancare;
                    if (tipSelectat.equals("Băutură")) return produs instanceof Bautura;
                    return false;
                })
                // Filtrul #4: Prețul
                .filter(produs -> {
                    try {
                        double minPret = pretMinField.getText().isEmpty() ? 0 : Double.parseDouble(pretMinField.getText());
                        double maxPret = pretMaxField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(pretMaxField.getText());
                        return produs.getPret() >= minPret && produs.getPret() <= maxPret;
                    } catch (NumberFormatException e) {
                        return true; // Dacă textul nu e un număr valid, ignorăm filtrul de preț
                    }
                })
                // La final, colectăm rezultatele într-o listă nouă
                .collect(Collectors.toList());

        // Actualizăm tabelul cu lista proaspăt filtrată
        produseAfisate.setAll(produseFiltrate);
    }

    /**
     * Actualizează panoul din dreapta cu detaliile produsului selectat.
     * @param produs Produsul selectat, sau null dacă nu e nimic selectat.
     */
    private void afiseazaDetalii(Produs produs) {
        if (produs != null) {
            numeDetaliiLabel.setText(produs.getNume());
            pretDetaliiLabel.setText(String.format("%.2f RON", produs.getPret()));
            categorieDetaliiLabel.setText(produs.getCategorie());

            // Folosim instanceof pentru a afișa detaliul specific
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
            // Dacă nu e nimic selectat, golim detaliile
            numeDetaliiLabel.setText("-");
            pretDetaliiLabel.setText("-");
            categorieDetaliiLabel.setText("-");
            specificatiiLabel.setText("Gramaj/Volum:");
            specificatiiDetaliiLabel.setText("-");
        }
    }
}