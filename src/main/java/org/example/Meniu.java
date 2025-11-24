package org.example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Meniu {

    public Map<String, List<Produs>> produse;

    public Meniu()
    {
        produse =  new HashMap<>();
    }

    public void adaugaProdus(Produs produs)
    {
        String categorie =  produs.getCategorie();
        List<Produs> listaProduse;
        if(produse.containsKey(categorie))
            listaProduse = produse.get(categorie);
        else
        {
            listaProduse = new ArrayList<>();
            produse.put(categorie, listaProduse);
        }
        listaProduse.add(produs);
    }

    public void afiseazaMeniu() {
        System.out.println("---Meniu Reastaurnatul lui Andrei---");
        for (Map.Entry<String, List<Produs>> entry : produse.entrySet()) {
            String categorie = entry.getKey();
            List<Produs> produse = entry.getValue();

            System.out.println("\n--- " + categorie.toUpperCase() + " ---");
            for (Produs produs : produse)
            {
                produs.afisareDetalii();
            }
        }
        System.out.println("\n-----------------------------");

    }

  public List<Produs> getProduse(String categorie)
    {
        return produse.getOrDefault(categorie, new ArrayList<>());
    }

    private List<Produs> getToateProdusele()
    {
        return produse.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }


    public double getPretMediuDeserturi() {
        return getProduse("Desert").stream()
                .mapToDouble(Produs::getPret)
                .average()
                .orElse(0.0);
    }
    public boolean existaProdusePesteSuta() {
        return getToateProdusele().stream()
                .anyMatch(p -> p.getPret() > 100);
    }

    public List<Mancare> getPreparateVegetarieneSortat() {
        return getToateProdusele().stream()
                .filter(p -> p instanceof Mancare)
                .map(p -> (Mancare) p)
                .filter(Mancare::getEsteVegetarian)
                .sorted(Comparator.comparing(Produs::getNume))
                .collect(Collectors.toList());
    }

    public Optional<Produs> cautaProdusDupaNume(String numeProdus)
    {
        return getToateProdusele().stream().filter(produs->produs.getNume().equalsIgnoreCase(numeProdus)).findFirst();
    }
    public void exportaMeniuInJSON(String caleFisier) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonText = gson.toJson(this.produse);
        try (FileWriter writer = new FileWriter(caleFisier)) {
            writer.write(jsonText);
            System.out.println("SUCCES: Meniul a fost exportat cu succes in fisierul '" + caleFisier + "'");
        } catch (IOException e) {
            System.err.println("EROARE: Nu s-a putut scrie in fisierul de export: " + e.getMessage());
        }
    }
}
