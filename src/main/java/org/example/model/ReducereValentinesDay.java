package org.example.model;

public class ReducereValentinesDay implements StrategieReduceri {
    private static final double PROCENT_REDUCERE = 0.10;

    @Override
    public double calculeazaReducere(Comanda comanda) {
        double subtotal = 0;
        for (ComandaItem item : comanda.getItems()) {
            subtotal += item.getPretLaVanzare() * item.getCantitate();
        }
        return subtotal * PROCENT_REDUCERE;
    }
}