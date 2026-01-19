package org.example.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("BAUTURA")

public final class Bautura extends Produs{
    private float volume;

    public Bautura() {

    }

    public Bautura(String nume, float pret, String categorie, float volume, String bauturi) {
        super(nume, pret, categorie);
        this.volume = volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public void afisareDetalii() {
        System.out.println("Nume: " + getNume() + ", Pret: " + getPret() + ", Volum: " + volume + "ml");    }

    public float getVolume() {
        return volume;
    }

    @Override
    public String obtineDetalii() {
        return String.format("> %s - %.1f RON - Volum: %.0fml", getNume(), getPret(), volume);
    }
}

