package org.example;

public abstract sealed class Produs permits Mancare, Bautura {
    String nume;
    float pret;

    public Produs(String nume, float pret) {
        this.nume = nume;
        this.pret = pret;
    }
    public void afisareDetalii() {
        System.out.println("Nume: " + nume + ", Pret: " + pret);
    }

    public String getNume() {
        return nume;
    }

    public float getPret() {
        return pret;
    }


}

