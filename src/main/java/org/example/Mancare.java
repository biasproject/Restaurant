package org.example;

public final class Mancare extends Produs{
    private float grmaj;
    private boolean esteVegetarian;
    public Mancare(String nume, float pret, String categorie, float gramaj, boolean esteVegetarian) {
        super(nume, pret, categorie);
        this.grmaj = gramaj;
        this.esteVegetarian = esteVegetarian;
    }

    public boolean getEsteVegetarian() {
        return esteVegetarian;
    }
    @Override
    public void afisareDetalii() {
        System.out.println("Nume: " + nume + ", Pret: " + pret + ", Gramaj: " + grmaj + "g");

    }

    @Override
    public String obtineDetalii() {
        return String.format("> %s - %.1f RON - Gramaj: %.0fg", nume, pret, grmaj);
    }
}
