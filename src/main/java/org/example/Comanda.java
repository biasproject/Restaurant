package org.example;

import org.example.ProdusComandat;

import java.util.ArrayList;
import java.util.List;


public class Comanda {


    private List<ProdusComandat> produseComandate;


    private StrategieReduceri strategieCurenta;

    public Comanda() {
        this.produseComandate = new ArrayList<>();
        this.strategieCurenta = new FaraReducere();
    }

    public void adaugaProdus(Produs produs, int cantitate) {
        this.produseComandate.add(new ProdusComandat(produs, cantitate));
    }

    public void setStrategieReducere(StrategieReduceri strategieNoua) {
        this.strategieCurenta = strategieNoua;
    }

    public List<ProdusComandat> getProduseComandate() {
        return produseComandate;
    }


    public void afiseazaTotal(double cotaTVA) {
        double subtotal = 0;
        System.out.println("----------- BON FISCAL -----------");
        for (ProdusComandat pc : produseComandate) {
            System.out.printf("%d x %s - %.2f RON%n",
                    pc.getCantitate(),
                    pc.getProdus().getNume(),
                    pc.getPretTotalProdus()
            );
            subtotal += pc.getPretTotalProdus();
        }
        System.out.println("----------------------------------");
        System.out.printf("SUBTOTAL: %.2f RON%n", subtotal);

        double valoareReducere = strategieCurenta.calculeazaReducere(this);

        if (valoareReducere > 0) {
            System.out.printf("REDUCERE APLICATA: -%.2f RON%n", valoareReducere);
        }

        double totalInainteDeTVA = subtotal - valoareReducere;
        double valoareTVA = totalInainteDeTVA * cotaTVA;
        double totalFinal = totalInainteDeTVA + valoareTVA;

        System.out.printf("TVA (%.0f%%): %.2f RON%n", cotaTVA * 100, valoareTVA);
        System.out.println("==================================");
        System.out.printf("TOTAL DE PLATA: %.2f RON%n", totalFinal);
        System.out.println("==================================");
    }
}