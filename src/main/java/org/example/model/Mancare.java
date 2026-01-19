package org.example.model;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("MANCARE")

public final class Mancare extends Produs{
    private float gramaj;
    private boolean esteVegetarian;
    public Mancare(String nume, float pret, String categorie, float gramaj, boolean esteVegetarian) {
        super(nume, pret, categorie);
        this.gramaj = gramaj;
        this.esteVegetarian = esteVegetarian;
    }


    public Mancare() {

    }

    public void setGramaj(float gramaj) {
        this.gramaj = gramaj;
    }

    public void setEsteVegetarian(boolean esteVegetarian) {
        this.esteVegetarian = esteVegetarian;
    }

    public float getGramaj() {
        return gramaj;
    }

    public boolean getEsteVegetarian() {
        return esteVegetarian;
    }
    @Override
    public void afisareDetalii() {
        System.out.println("Nume: " + getNume() + ", Pret: " + getPret() + ", Gramaj: " + getGramaj() + "g");

    }

    @Override
    public String obtineDetalii() {
        return String.format("> %s - %.1f RON - Gramaj: %.0fg", getNume(), getPret(), getGramaj());
    }
}
