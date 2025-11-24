package org.example;

public abstract sealed class Produs permits Bautura, Mancare, Pizza {
    protected String nume;
    protected float pret;
    protected String categorie;

    public Produs(String nume, float pret, String categorie) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
    }

    protected Produs() {
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

    public String getCategorie() { return  categorie; }

    // --- Partea 2: Metodele publice ale clasei Pizza (de adaugat) ---
    public abstract String obtineDetalii();
}

