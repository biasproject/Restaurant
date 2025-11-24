package org.example;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("Se încarcă configurarea...");
        Optional<Configuratie> optionalConfig = IncarcatorConfiguratie.incarcaConfiguratie("config.json");

        if (optionalConfig.isEmpty()) {
            System.err.println("Aplicatia nu poate porni din cauza erorilor de configurare.");
            System.err.println("Vă rugăm contactați suportul tehnic.");
            return;
        }

        Configuratie config = optionalConfig.get();
        String numeRestaurant = config.getNumeRestaurant();
        double cotaTVA = config.getCotaTVA();

        System.out.println("Bun venit la " + numeRestaurant + "!");
        System.out.println("Cota de TVA aplicată astăzi este: " + (cotaTVA * 100) + "%");

        Meniu meniuRestaurant = new Meniu();
        meniuRestaurant.adaugaProdus(new Mancare("Hummus", 32.0f, "Aperitive", 250, true));
        meniuRestaurant.adaugaProdus(new Mancare("Friptura de vita", 115.0f, "Fel principal", 350, false));
        meniuRestaurant.adaugaProdus(new Bautura("Limonada", 18.0f, "Bautura non-alcoolica", 250, "Bauturi"));
        Pizza pizzaCasei = new Pizza.PizzaBuilder("Pufos", "Rosii")
                .withExtraMozzarella()
                .withSalam()
                .build();
        meniuRestaurant.adaugaProdus(pizzaCasei);

        meniuRestaurant.afiseazaMeniu();

        System.out.println("\n--- Rapoarte de management ---");
        System.out.println("\nPreparate vegetariene sortate:");
        List<Mancare> vegetariene = meniuRestaurant.getPreparateVegetarieneSortat();
        vegetariene.forEach(m -> System.out.println("  - " + m.getNume()));

        System.out.println("\nExista produse mai scumpe de 100 RON?");
        System.out.println("  - " + (meniuRestaurant.existaProdusePesteSuta() ? "Da" : "Nu"));

        System.out.println("\n--- Căutare produs ---");
        System.out.println("\nCăutare pentru 'Friptura de vita':");
        Optional<Produs> produsGasit = meniuRestaurant.cautaProdusDupaNume("Friptura de vita");
        produsGasit.ifPresent(p -> System.out.println("  - Găsit: " + p.getNume() + ", Pret: " + p.getPret()));

        System.out.println("\n--- Comanda client ---");
        Comanda comandaClient = new Comanda();
        comandaClient.adaugaProdus(pizzaCasei, 1);
        comandaClient.adaugaProdus(meniuRestaurant.cautaProdusDupaNume("Limonada").get(), 2);

        System.out.println("\nSe aplică oferta zilei: 10% reducere!");
        comandaClient.setStrategieReducere(new ReducereValentinesDay());

        comandaClient.afiseazaTotal(cotaTVA);

        System.out.println("\n--- Export meniu ---");
        meniuRestaurant.exportaMeniuInJSON("meniu_exportat.json");
    }
}