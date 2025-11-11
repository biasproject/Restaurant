package org.example;

public final class Bautura extends Produs{
    float volume;
    public Bautura(String nume, float pret, float volume) {
        super(nume, pret);
        this.volume = volume;
    }
    @Override
    public void afisareDetalii() {
        System.out.println("Nume: " + nume + ", Pret: " + pret + ", Volum: " + volume + "ml");
    }
}

