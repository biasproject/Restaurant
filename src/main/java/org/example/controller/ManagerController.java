package org.example.controller;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import javafx.concurrent.Task; // Adaugă acest import
import org.example.model.*;
import org.example.util.JsonMenuIO;
import org.example.gui.RestaurantApp; // import pentru executor și resurse


public class ManagerController {

    // --- Repositories (Departamentele de logistică) ---
    private ProdusRepository produsRepository;
    private UserRepository userRepository;

    // --- Tab 1: Gestiune Meniu ---
    @FXML private TableView<Produs> meniuTableView;
    @FXML private TableColumn<Produs, String> meniuNumeColumn;
    @FXML private TableColumn<Produs, String> meniuCategorieColumn;
    @FXML private TableColumn<Produs, Float> meniuPretColumn;
    private ObservableList<Produs> produseAfisate;

    // --- Tab 2: Gestiune Personal ---
    @FXML private TableView<User> personalTableView;
    @FXML private TableColumn<User, String> personalUsernameColumn;
    @FXML private TableColumn<User, UserRole> personalRolColumn;
    private ObservableList<User> personalAfisat;

    // --- Tab 3: Gestiune Oferte (momentan doar UI) ---
    @FXML private CheckBox happyHourCheck;
    @FXML private CheckBox mealDealCheck;
    @FXML private CheckBox partyPackCheck;

    @FXML private ProgressIndicator loadingIndicator; // added field

    // --- Tab 4: Istoric Global ---
    @FXML private TableView<Comanda> comenziTable;
    @FXML private TableColumn<Comanda, Integer> colComId;
    @FXML private TableColumn<Comanda, String> colComUser;
    @FXML private TableColumn<Comanda, Integer> colComMasa;
    @FXML private TableColumn<Comanda, Double> colComSubtotal;
    @FXML private TableColumn<Comanda, Double> colComReduceri;
    @FXML private TableColumn<Comanda, Double> colComTotal;
    @FXML private TableColumn<Comanda, String> colComData;

    private ObservableList<Comanda> comenziAfisate;

    @FXML
    public void initialize() {
        produsRepository = new ProdusRepository();
        userRepository = new UserRepository();

        setupMeniuTab();
        setupPersonalTab();
        setupComenziTab();
        OfferManager offerManager = OfferManager.getInstance();
        happyHourCheck.setSelected(offerManager.isHappyHourActive());
        mealDealCheck.setSelected(offerManager.isMealDealActive());
        partyPackCheck.setSelected(offerManager.isPartyPackActive());
    }

    // ========================================================================
    // LOGICA PENTRU GESTIUNE MENIU
    // ========================================================================
    private void setupMeniuTab() {
        meniuNumeColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        meniuCategorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        meniuPretColumn.setCellValueFactory(new PropertyValueFactory<>("pret"));

        // Initial load async
        reloadMenuAsync();
    }

    private void incarcaDateMeniu() {
        // kept for backward compatibility but prefer reloadMenuAsync
        produseAfisate = FXCollections.observableArrayList(produsRepository.gasesteTot());
        meniuTableView.setItems(produseAfisate);
    }

    // New: load products asynchronously and update UI
    private void reloadMenuAsync() {
        Task<List<Produs>> task = new Task<>() {
            @Override
            protected List<Produs> call() throws Exception {
                return produsRepository.gasesteTot();
            }
        };

        task.setOnRunning(e -> loadingIndicator.setVisible(true));
        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            List<Produs> list = task.getValue();
            if (produseAfisate == null) produseAfisate = FXCollections.observableArrayList();
            produseAfisate.setAll(list);
            meniuTableView.setItems(produseAfisate);
            meniuTableView.refresh();
        });
        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            task.getException().printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-au putut încărca produsele: " + task.getException().getMessage());
        });

        RestaurantApp.executor.submit(task);
    }

    @FXML
    private void handleAdaugaProdus() {
        Produs rezultat = showProdusDialog(null);
        if (rezultat != null) {
            // immediate UI feedback: add locally
            if (produseAfisate == null) produseAfisate = FXCollections.observableArrayList();
            produseAfisate.add(rezultat);
            Platform.runLater(() -> { meniuTableView.setItems(produseAfisate); meniuTableView.refresh(); });

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    produsRepository.salveaza(rezultat);
                    return null;
                }
            };
            task.setOnRunning(e -> loadingIndicator.setVisible(true));
            task.setOnSucceeded(e -> {
                loadingIndicator.setVisible(false);
                // reload to get DB-assigned id and consistent state
                reloadMenuAsync();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Produsul a fost adăugat cu succes.");
            });
            task.setOnFailed(e -> {
                loadingIndicator.setVisible(false);
                Throwable ex = task.getException();
                // keep local addition but inform user
                if (ex instanceof Exception) showExceptionAlert((Exception) ex, "Eroare la salvare produs — s-a păstrat local");
            });
            RestaurantApp.executor.submit(task);
        }
    }

    @FXML
    private void handleModificaProdus() {
        Produs selectat = meniuTableView.getSelectionModel().getSelectedItem();
        if (selectat == null) {
            showAlert(Alert.AlertType.WARNING, "Nicio selecție", "Selectați un produs pentru a-l modifica.");
            return;
        }

        Produs rezultat = showProdusDialog(selectat);
        if (rezultat != null) {
            // immediate UI update
            if (produseAfisate == null) produseAfisate = FXCollections.observableArrayList();
            boolean replaced = false;
            for (int i = 0; i < produseAfisate.size(); i++) {
                Produs p = produseAfisate.get(i);
                if (p.getId() == rezultat.getId()) {
                    produseAfisate.set(i, rezultat);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) produseAfisate.add(rezultat);
            Platform.runLater(() -> { meniuTableView.setItems(produseAfisate); meniuTableView.refresh(); });

            Task<Produs> task = new Task<>() {
                @Override
                protected Produs call() throws Exception {
                    return produsRepository.update(rezultat);
                }
            };
            task.setOnRunning(e -> loadingIndicator.setVisible(true));
            task.setOnSucceeded(e -> {
                loadingIndicator.setVisible(false);
                // reload to ensure DB consistency
                reloadMenuAsync();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Produsul a fost actualizat.");
            });
            task.setOnFailed(e -> {
                loadingIndicator.setVisible(false);
                Throwable ex = task.getException();
                // inform user and keep local change
                if (ex instanceof Exception) showExceptionAlert((Exception) ex, "Eroare la actualizare produs — s-a păstrat local");
            });
            RestaurantApp.executor.submit(task);
        }
    }

    @FXML
    private void handleStergeProdus() {
        Produs selectat = meniuTableView.getSelectionModel().getSelectedItem();
        if (selectat == null) {
            showAlert(Alert.AlertType.WARNING, "Nicio selecție", "Selectați un produs pentru a-l șterge.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Ștergere produs");
        confirm.setHeaderText("Sigur doriți să ștergeți produsul '" + selectat.getNume() + "' ?");
        Optional<ButtonType> rez = confirm.showAndWait();
        if (!(rez.isPresent() && rez.get() == ButtonType.OK)) return;

        // immediate UI removal
        if (produseAfisate != null) produseAfisate.removeIf(p -> p.getId() == selectat.getId());
        Platform.runLater(() -> { meniuTableView.getItems().removeIf(p -> p.getId() == selectat.getId()); meniuTableView.refresh(); });

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                produsRepository.sterge(selectat);
                return null;
            }
        };
        task.setOnRunning(e -> loadingIndicator.setVisible(true));
        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            // reload to ensure DB consistency
            reloadMenuAsync();
            showAlert(Alert.AlertType.INFORMATION, "Șters", "Produsul a fost șters cu succes.");
        });
        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            Throwable ex = task.getException();
            // rollback local removal by reloading
            reloadMenuAsync();
            if (ex instanceof Exception) showExceptionAlert((Exception) ex, "Eroare la ștergerea produsului — rollback efectuat");
        });
        RestaurantApp.executor.submit(task);
    }

    // --- helper to show exception details in an Alert ---
    private void showExceptionAlert(Exception e, String title) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(e.getClass().getSimpleName() + ": " + e.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        alert.getDialogPane().setExpandableContent(textArea);

        alert.showAndWait();
    }

    // Helper: dialog pentru creare / editare produs
    private Produs showProdusDialog(Produs existing) {
        Dialog<Produs> dialog = new Dialog<>();
        // set owner to main window so dialog is modal and in front
        Window owner = meniuTableView != null && meniuTableView.getScene() != null ? meniuTableView.getScene().getWindow() : null;
        if (owner != null) dialog.initOwner(owner);

        dialog.setTitle(existing == null ? "Adaugă produs" : "Modifică produs");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Fields comune
        TextField numeField = new TextField();
        TextField pretField = new TextField();
        TextField categorieField = new TextField();

        // Tipuri specifice: choice
        ChoiceBox<String> tipBox = new ChoiceBox<>(FXCollections.observableArrayList("Pizza", "Mancare", "Bautura"));
        tipBox.setValue("Mancare");

        // Specific fields
        TextField blatField = new TextField();
        TextField sosField = new TextField();
        CheckBox extraMozzCheck = new CheckBox("Extra mozzarella");
        CheckBox ciuperciCheck = new CheckBox("Ciuperci");
        CheckBox salamCheck = new CheckBox("Salam");
        CheckBox ananasCheck = new CheckBox("Ananas");

        TextField gramajField = new TextField();
        CheckBox vegetarianCheck = new CheckBox("Vegetarian");

        TextField volumeField = new TextField();

        // Populate if edit
        if (existing != null) {
            numeField.setText(existing.getNume());
            pretField.setText(String.valueOf(existing.getPret()));
            categorieField.setText(existing.getCategorie());
            if (existing instanceof Pizza) {
                tipBox.setValue("Pizza");
                Pizza p = (Pizza) existing;
                blatField.setText(p.getBlat());
                sosField.setText(p.getSos());
                extraMozzCheck.setSelected(p.isAreExtraMozzarella());
                ciuperciCheck.setSelected(p.isAreCiuperci());
                salamCheck.setSelected(p.isAreSalam());
                ananasCheck.setSelected(p.isAreAnanas());
            } else if (existing instanceof Mancare) {
                tipBox.setValue("Mancare");
                Mancare m = (Mancare) existing;
                gramajField.setText(String.valueOf(m.getGramaj()));
                vegetarianCheck.setSelected(m.getEsteVegetarian());
            } else if (existing instanceof Bautura) {
                tipBox.setValue("Bautura");
                Bautura b = (Bautura) existing;
                volumeField.setText(String.valueOf(b.getVolume()));
            }
        }

        // Layout in dialog (simple vertical box)
        VBox content = new VBox(8);
        content.getChildren().addAll(new Label("Nume:"), numeField,
                new Label("Pret:"), pretField,
                new Label("Categorie:"), categorieField,
                new Label("Tip produs:"), tipBox,
                new Label("--- Pizza specific ---"), new Label("Blat:"), blatField, new Label("Sos:"), sosField,
                extraMozzCheck, ciuperciCheck, salamCheck, ananasCheck,
                new Label("--- Mancare specific ---"), new Label("Gramaj:"), gramajField, vegetarianCheck,
                new Label("--- Bautura specific ---"), new Label("Volum (ml):"), volumeField
        );

        dialog.getDialogPane().setContent(content);

        // Convert result
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    String nume = numeField.getText();
                    float pret = Float.parseFloat(pretField.getText());
                    String categorie = categorieField.getText().isEmpty() ? "Altele" : categorieField.getText();
                    String tip = tipBox.getValue();

                    if (tip.equals("Pizza")) {
                        String blat = blatField.getText().isEmpty() ? "Standard" : blatField.getText();
                        String sos = sosField.getText().isEmpty() ? "Rosii" : sosField.getText();
                        boolean extra = extraMozzCheck.isSelected();
                        boolean ciuperci = ciuperciCheck.isSelected();
                        boolean salam = salamCheck.isSelected();
                        boolean ananas = ananasCheck.isSelected();
                        if (existing instanceof Pizza) {
                            Pizza p = (Pizza) existing;
                            p.setNume(nume); p.setPret(pret); p.setCategorie(categorie);
                            p.setBlat(blat); p.setSos(sos);
                            p.setAreExtraMozzarella(extra); p.setAreCiuperci(ciuperci); p.setAreSalam(salam); p.setAreAnanas(ananas);
                            return p;
                        }
                        return new Pizza(nume, pret, categorie, blat, sos, extra, ciuperci, salam, ananas);
                    } else if (tip.equals("Mancare")) {
                        float gramaj = gramajField.getText().isEmpty() ? 0f : Float.parseFloat(gramajField.getText());
                        boolean veg = vegetarianCheck.isSelected();
                        if (existing instanceof Mancare) {
                            Mancare m = (Mancare) existing;
                            m.setNume(nume); m.setPret(pret); m.setCategorie(categorie);
                            m.setGramaj(gramaj); m.setEsteVegetarian(veg);
                            return m;
                        }
                        return new Mancare(nume, pret, categorie, gramaj, veg);
                    } else { // Bautura
                        float vol = volumeField.getText().isEmpty() ? 0f : Float.parseFloat(volumeField.getText());
                        if (existing instanceof Bautura) {
                            Bautura b = (Bautura) existing;
                            b.setNume(nume); b.setPret(pret); b.setCategorie(categorie);
                            b.setVolume(vol);
                            return b;
                        }
                        return new Bautura(nume, pret, categorie, vol, categorie);
                    }
                } catch (NumberFormatException nfe) {
                    showAlert(Alert.AlertType.ERROR, "Input invalid", "Prețul/numerele nu sunt valide.");
                    return null;
                }
            }
            return null;
        });

        Optional<Produs> result = dialog.showAndWait();
        return result.orElse(null);
    }

    @FXML
    private void handleImportJson() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import Meniu (JSON)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        Stage st = (Stage) meniuTableView.getScene().getWindow();
        File chosen = chooser.showOpenDialog(st);
        if (chosen == null) return;

        Task<List<Produs>> importTask = new Task<>() {
            @Override
            protected List<Produs> call() throws Exception {
                return JsonMenuIO.importMenu(chosen, produsRepository);
            }
        };

        importTask.setOnRunning(e -> loadingIndicator.setVisible(true));
        importTask.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            List<Produs> created = importTask.getValue();
            if (created.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Import", "Fișierul nu conține produse sau formatul nu e recunoscut.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Import", "S-au importat " + created.size() + " produse.");
            }
            incarcaDateMeniu();
        });
        importTask.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            showAlert(Alert.AlertType.ERROR, "Eroare Import", "A apărut o eroare la import: " + importTask.getException().getMessage());
            importTask.getException().printStackTrace();
        });

        RestaurantApp.executor.submit(importTask);
    }

    @FXML
    private void handleExportJson() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Meniu (JSON)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        Stage st = (Stage) meniuTableView.getScene().getWindow();
        File chosen = chooser.showSaveDialog(st);
        if (chosen == null) return;

        Task<Void> exportTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                JsonMenuIO.exportMenu(produseAfisate, chosen);
                return null;
            }
        };

        exportTask.setOnRunning(e -> loadingIndicator.setVisible(true));
        exportTask.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            showAlert(Alert.AlertType.INFORMATION, "Export", "Meniul a fost exportat cu succes: " + chosen.getAbsolutePath());
        });
        exportTask.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            showAlert(Alert.AlertType.ERROR, "Eroare Export", "A apărut o eroare la export: " + exportTask.getException().getMessage());
            exportTask.getException().printStackTrace();
        });

        RestaurantApp.executor.submit(exportTask);
    }


    // ========================================================================
    // LOGICA PENTRU GESTIUNE PERSONAL
    // ========================================================================
    private void setupPersonalTab() {
        personalUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        personalRolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));

        incarcaDatePersonal();
    }


    private void incarcaDatePersonal() {
        // Creăm o sarcină nouă (Task) care va rula în fundal
        Task<List<User>> loadUsersTask = new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                // Aceasta este munca grea. Rulează pe un fir de fundal.
                // NU avem voie să atingem interfața grafică de aici!
                return userRepository.findAll();
            }
        };

        // Ce se întâmplă CÂND sarcina pornește
        loadUsersTask.setOnRunning(event -> {
            loadingIndicator.setVisible(true); // Afișăm cerculețul de încărcare
        });

        // Ce se întâmplă CÂND sarcina se termină cu SUCCES
        loadUsersTask.setOnSucceeded(event -> {
            loadingIndicator.setVisible(false); // Ascundem cerculețul
            personalAfisat = FXCollections.observableArrayList(loadUsersTask.getValue());
            personalTableView.setItems(personalAfisat);
        });

        // Ce se întâmplă CÂND sarcina EȘUEAZĂ
        loadUsersTask.setOnFailed(event -> {
            loadingIndicator.setVisible(false);
            showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-au putut încărca datele personalului.");
            loadUsersTask.getException().printStackTrace();
        });

        // Pornim sarcina pe un fir de execuție din "echipa" noastră
        RestaurantApp.executor.submit(loadUsersTask);
    }

    @FXML
    private void handleAdaugaPersonal() {
        // TODO: Deschide o fereastră nouă pentru a adăuga un user (ospătar)
        showAlert(Alert.AlertType.INFORMATION, "Info", "Funcționalitate în construcție!");
    }

    @FXML
    private void handleModificaPersonal() {
        // TODO: Deschide o fereastră nouă pentru a modifica user-ul selectat
        showAlert(Alert.AlertType.INFORMATION, "Info", "Funcționalitate în construcție!");
    }

    @FXML
    private void handleStergePersonal() {
        User userSelectat = personalTableView.getSelectionModel().getSelectedItem();
        if (userSelectat == null) {
            showAlert(Alert.AlertType.WARNING, "Nicio selecție", "Vă rugăm selectați un angajat din listă.");
            return;
        }

        // Aici implementăm confirmarea critică cerută în barem
        Alert alertConfirmare = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirmare.setTitle("Confirmare Concediere");
        alertConfirmare.setHeaderText("Sunteți sigur că doriți să concediați angajatul '" + userSelectat.getUsername() + "'?");
        alertConfirmare.setContentText("ATENȚIE: Această acțiune este ireversibilă și va șterge permanent tot istoricul de comenzi asociat acestui angajat!");

        Optional<ButtonType> rezultat = alertConfirmare.showAndWait();
        if (rezultat.isPresent() && rezultat.get() == ButtonType.OK) {
            // Utilizatorul a confirmat ștergerea
            try {
                userRepository.delete(userSelectat);
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Angajatul a fost concediat.");
                // Reîncărcăm lista pentru a reflecta schimbarea
                incarcaDatePersonal();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Eroare DB", "A apărut o eroare la ștergerea din baza de date.");
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void handleLogout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApp.class.getResource("/org/example/gui/LoginView.fxml"));
        Stage stage = (Stage) meniuTableView.getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setTitle("Autentificare");
        stage.show();
    }

    // ========================================================================
    // LOGICA PENTRU GESTIUNE OFERTE
    // ========================================================================
    @FXML
    private void handleSalveazaOferte() {
        OfferManager offerManager = OfferManager.getInstance();
        offerManager.setHappyHourActive(happyHourCheck.isSelected());
        offerManager.setMealDealActive(mealDealCheck.isSelected());
        offerManager.setPartyPackActive(partyPackCheck.isSelected());

        showAlert(Alert.AlertType.INFORMATION, "Succes", "Starea ofertelor a fost salvată pentru sesiunea curentă.");
    }

    private void setupComenziTab() {
        colComId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colComUser.setCellValueFactory(cell -> {
            Comanda c = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(c.getUser() != null ? c.getUser().getUsername() : "");
        });
        colComMasa.setCellValueFactory(new PropertyValueFactory<>("numarMasa"));
        colComSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colComReduceri.setCellValueFactory(new PropertyValueFactory<>("totalReduceri"));
        colComTotal.setCellValueFactory(new PropertyValueFactory<>("totalFinal"));
        colComData.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataPlasare().toString()));

        reloadComenziAsync();
    }

    private void reloadComenziAsync() {
        ComandaRepository comandaRepository = new ComandaRepository();
        Task<List<Comanda>> task = new Task<>() {
            @Override
            protected List<Comanda> call() throws Exception {
                return comandaRepository.findAll();
            }
        };
        task.setOnRunning(e -> loadingIndicator.setVisible(true));
        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            List<Comanda> list = task.getValue();
            if (comenziAfisate == null) comenziAfisate = FXCollections.observableArrayList();
            comenziAfisate.setAll(list);
            comenziTable.setItems(comenziAfisate);
            comenziTable.refresh();
        });
        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            task.getException().printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-au putut încărca comenzile: " + task.getException().getMessage());
        });
        RestaurantApp.executor.submit(task);
    }


    // --- Metodă ajutătoare ---
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

