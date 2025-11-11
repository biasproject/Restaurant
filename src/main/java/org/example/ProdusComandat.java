package org.example;


public class ProdusComandat{
    private Produs produs;
    private int cantitate;

    public ProdusComandat(Produs produs, int cantitate) {
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Produs getProdus() {
        return produs;
    }

    public int getCantitate() {
        return cantitate;
    }


    public double getPretTotalProdus() {
        return produs.getPret() * cantitate;
    }
}
