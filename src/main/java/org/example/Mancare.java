package org.example;

public final class Mancare extends Produs{
    float grmaj;
    public Mancare(String nume, float pret, float gramaj){
        super(nume, pret);
        this.grmaj = gramaj;
    }
    @Override
    public void afisareDetalii() {
        System.out.println("Nume: " + nume + ", Pret: " + pret + ", Gramaj: " + grmaj + "g");

    }
}
