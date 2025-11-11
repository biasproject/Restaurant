package org.example;

import java.util.Vector;

public class Meniu {
    public Vector<Produs> produse;

    public Meniu()
    {
        produse =  new Vector<>();
    }

    public void adaugaProdus(Produs produs)
    {
        produse.add(produs);
    }

    public void afiseazaMeniu() {
        System.out.println("---Meniu Reastaurnatul lui Andrei---");
        for (Produs produs : produse)
        {
            produs.afisareDetalii();
        }
    }

    public Vector<Produs> getProduse() {
        return produse;
    }
}
