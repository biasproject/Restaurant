package org.example.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("PIZZA")
public final class Pizza extends Produs {
    private  String blat;
    private  String sos;

    private  boolean areExtraMozzarella;
    private  boolean areCiuperci;
    private  boolean areSalam;
    private  boolean areAnanas;

    public Pizza(String nume, double pret, String categorie,
                  String blat, String sos,
                  boolean areExtraMozzarella, boolean areCiuperci,
                  boolean areSalam, boolean areAnanas) {
        super(nume, (float) pret, categorie);
        this.blat = blat;
        this.sos = sos;
        this.areExtraMozzarella = areExtraMozzarella;
        this.areCiuperci = areCiuperci;
        this.areSalam = areSalam;
        this.areAnanas = areAnanas;
    }
    public Pizza()
    {

    }


    public String getBlat() { return blat; }
    public void setBlat(String blat) { this.blat = blat; }

    public String getSos() { return sos; }
    public void setSos(String sos) { this.sos = sos; }

    public boolean isAreExtraMozzarella() { return areExtraMozzarella; }
    public void setAreExtraMozzarella(boolean areExtraMozzarella) { this.areExtraMozzarella = areExtraMozzarella; }

    public boolean isAreCiuperci() { return areCiuperci; }
    public void setAreCiuperci(boolean areCiuperci) { this.areCiuperci = areCiuperci; }

    public boolean isAreSalam() { return areSalam; }
    public void setAreSalam(boolean areSalam) { this.areSalam = areSalam; }

    public boolean isAreAnanas() { return areAnanas; }
    public void setAreAnanas(boolean areAnanas) { this.areAnanas = areAnanas; }

    @Override
    public String obtineDetalii() {
        return "Pizza customizabila: " + getNume();
    }

    public void afiseazaDetaliiPizza() {
        System.out.println("--- Detalii Pizza: " + getNume() + " ---");
        System.out.println("  - Pret: " + getPret() + " RON");
        System.out.println("  - Blat: " + blat);
        System.out.println("  - Sos: " + sos);
        System.out.println("  - Topping-uri:");
        if (areExtraMozzarella) System.out.println("    + Extra Mozzarella");
        if (areCiuperci) System.out.println("    + Ciuperci");
        if (areSalam) System.out.println("    + Salam");
        if (areAnanas) System.out.println("    + Ananas");
        System.out.println("---------------------------------");
    }

    public static class PizzaBuilder {
        private final String blat;
        private final String sos;

        private boolean areExtraMozzarella = false;
        private boolean areCiuperci = false;
        private boolean areSalam = false;
        private boolean areAnanas = false;

        private String nume = "Pizza Custom";
        private double pretCurent = 15.0;
        private String categorie = "Pizza";

        public PizzaBuilder(String blat, String sos) {
            this.blat = blat;
            this.sos = sos;
        }


        public PizzaBuilder withExtraMozzarella() {
            this.areExtraMozzarella = true;
            this.pretCurent += 5.0;
            return this;
        }

        public PizzaBuilder withCiuperci() {
            this.areCiuperci = true;
            this.pretCurent += 4.0;
            return this;
        }

        public PizzaBuilder withSalam() {
            this.areSalam = true;
            this.pretCurent += 6.0;
            return this;
        }

        public PizzaBuilder withAnanas() {
            this.areAnanas = true;
            this.pretCurent += 4.5;
            return this;
        }

        public Pizza build() {
            return new Pizza(nume, pretCurent, categorie,
                    blat, sos,
                    areExtraMozzarella, areCiuperci, areSalam, areAnanas);
        }
    }
}