package org.example;

public final class Pizza extends Produs {
    // --- Partea 1: Clasa principala (deja scrisa de tine si corectata) ---
    private final String blat;
    private final String sos;

    private final boolean areExtraMozzarella;
    private final boolean areCiuperci;
    private final boolean areSalam;
    private final boolean areAnanas;

    private Pizza(String nume, double pret, String categorie,
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

    // --- Partea 2: Metodele publice ale clasei Pizza (de adaugat) ---
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

    // --- Partea 3: Clasa Builder (de adaugat) ---
    // Aceasta este o clasa definita IN INTERIORUL clasei Pizza
    public static class PizzaBuilder {
        // Campurile builder-ului (NU sunt final)
        private final String blat; // Blatul si sosul sunt obligatorii, deci le facem final si in builder
        private final String sos;

        private boolean areExtraMozzarella = false;
        private boolean areCiuperci = false;
        private boolean areSalam = false;
        private boolean areAnanas = false;

        // Atributele produsului general
        private String nume = "Pizza Custom";
        private double pretCurent = 15.0; // Pret de baza pentru blat si sos
        private String categorie = "Pizza";

        // Constructorul builder-ului cere elementele OBLIGATORII
        public PizzaBuilder(String blat, String sos) {
            this.blat = blat;
            this.sos = sos;
        }

        // Metode pentru fiecare topping OPTIONAL
        // Fiecare metoda returneaza 'this' pentru a permite inlantuirea
        public PizzaBuilder withExtraMozzarella() {
            this.areExtraMozzarella = true;
            this.pretCurent += 5.0; // Adaugam costul topping-ului la pret
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

        // Metoda finala care CONSTRUIESTE obiectul Pizza
        public Pizza build() {
            // Apeleaza constructorul privat al clasei Pizza,
            // folosind toate datele adunate in builder
            return new Pizza(nume, pretCurent, categorie,
                    blat, sos,
                    areExtraMozzarella, areCiuperci, areSalam, areAnanas);
        }
    }
}