package org.example;

public class ReducereValentinesDay implements StrategieReduceri {
    private static final double PROCENT_REDUCERE = 0.10; // 10%

    @Override
    public double calculeazaReducere(Comanda comanda) {
        // Calculam subtotalul comenzii
        double subtotal = 0;
        for (ProdusComandat pc : comanda.getProduseComandate()) {
            subtotal += pc.getPretTotalProdus();
        }

        // Returnam 10% din valoarea totala
        return subtotal * PROCENT_REDUCERE;
    }
}
