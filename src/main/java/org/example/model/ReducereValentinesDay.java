package org.example.model;

public class ReducereValentinesDay implements StrategieReduceri {
    private static final double PROCENT_REDUCERE = 0.10; // 10%

    @Override
    public double calculeazaReducere(Comanda comanda) {
        // Logica de calcul a subtotalului este acum direct în clasa Comanda.
        // Dar pentru a păstra logica ta originală, o vom reface aici.

        double subtotal = 0;

        // MODIFICAREA CHEIE:
        // În loc de 'comanda.getProduseComandate()' care returna List<ProdusComandat>,
        // acum folosim 'comanda.getItems()' care returnează List<ComandaItem>.
        for (ComandaItem item : comanda.getItems()) {
            // Fiecare item are deja prețul la vânzare și cantitatea.
            subtotal += item.getPretLaVanzare() * item.getCantitate();
        }

        // Restul logicii rămâne la fel!
        // Returnam 10% din valoarea totala
        return subtotal * PROCENT_REDUCERE;
    }
}