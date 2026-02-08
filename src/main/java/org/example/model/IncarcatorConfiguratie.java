package org.example.model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class IncarcatorConfiguratie {

    public static Optional<Configuratie> incarcaConfiguratie(String caleFisier) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(caleFisier)) {
            Configuratie config = gson.fromJson(reader, Configuratie.class);
            return Optional.of(config);

        } catch (FileNotFoundException e) {
            System.err.println("EROARE CRITICA: Fisierul de configurare '" + caleFisier + "' lipseste!");
            return Optional.empty();

        } catch (JsonSyntaxException e) {
            System.err.println("EROARE CRITICA: Fisierul de configurare '" + caleFisier + "' este corupt sau formatat gresit!");
            return Optional.empty();

        } catch (IOException e) {
            System.err.println("Eroare la citirea fisierului de configurare: " + e.getMessage());
            return Optional.empty();
        }
    }
}