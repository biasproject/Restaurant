package org.example;

import org.example.Comanda;
import org.example.Mancare;

public class Main {
    public static void main(String[] args) {
        Mancare paste = new Mancare("Paste Carbonara", 42.0f, 400);
        Bautura vin = new Bautura("Pahar de vin rosu", 25.0f, 0.2f);

        System.out.println("\n");

        System.out.println(">>> SCENARIUL 4: Este Valentine's Day (10% reducere totala) <<<");
        Comanda comandaValentines = new Comanda();
        comandaValentines.adaugaProdus(paste, 2);  // 2 x 42 = 84 RON
        comandaValentines.adaugaProdus(vin, 2);    // 2 x 25 = 50 RON


        comandaValentines.setStrategieReducere(new ReducereValentinesDay());


        comandaValentines.afiseazaTotal();
    }
}