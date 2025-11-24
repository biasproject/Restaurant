package org.example;

public final class Bautura extends Produs{
    float volume;
    public Bautura(String nume, float pret, String categorie, float volume, String bauturi) {
        super(nume, pret, categorie);
        this.volume = volume;
    }
    @Override
    public void afisareDetalii() {
        System.out.println("Nume: " + nume + ", Pret: " + pret + ", Volum: " + volume + "ml");
    }

    @Override
    public String obtineDetalii() {
        return String.format("> %s - %.1f RON - Volum: %.0fml", nume, pret, volume);
    }
}

