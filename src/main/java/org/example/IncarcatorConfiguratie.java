package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException; // Importam eroarea specifica pentru JSON
import java.io.FileNotFoundException; // Importam eroarea specifica pentru fisier lipsa
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional; // Vom returna un Optional

public class IncarcatorConfiguratie {

    public static Optional<Configuratie> incarcaConfiguratie(String caleFisier) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(caleFisier)) {
            // Daca totul merge bine, citim fisierul
            Configuratie config = gson.fromJson(reader, Configuratie.class);
            // Si returnam o "cutie" plina cu configuratia
            return Optional.of(config);

        } catch (FileNotFoundException e) {
            // Eroare specifica: fisierul nu a fost gasit
            System.err.println("EROARE CRITICA: Fisierul de configurare '" + caleFisier + "' lipseste!");
            // Returnam o "cutie" goala pentru a semnala eroarea
            return Optional.empty();

        } catch (JsonSyntaxException e) {
            // Eroare specifica: fisierul exista, dar e scris gresit (JSON corupt)
            System.err.println("EROARE CRITICA: Fisierul de configurare '" + caleFisier + "' este corupt sau formatat gresit!");
            // Returnam o "cutie" goala
            return Optional.empty();

        } catch (IOException e) {
            // O alta eroare generica de citire
            System.err.println("Eroare la citirea fisierului de configurare: " + e.getMessage());
            // Returnam o "cutie" goala
            return Optional.empty();
        }
    }
}