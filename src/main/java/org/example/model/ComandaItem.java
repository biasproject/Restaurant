package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "comanda_items")
public class ComandaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produs_id", nullable = true)
    private Produs produs;

    private int cantitate;

    private double pretLaVanzare;

    private String numeProdusLaVanzare;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;

    public ComandaItem() {}

    public ComandaItem(Produs produs, int cantitate, Comanda comanda) {
        this.produs = produs;
        this.numeProdusLaVanzare = produs != null ? produs.getNume() : "";
        this.cantitate = cantitate;
        this.pretLaVanzare = produs != null ? produs.getPret() : 0.0;
        this.comanda = comanda;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
    }

    public double getPretLaVanzare() {
        return pretLaVanzare;
    }

    public void setPretLaVanzare(double pretLaVanzare) {
        this.pretLaVanzare = pretLaVanzare;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public String getNumeProdusLaVanzare() {
        return numeProdusLaVanzare;
    }

    public void setNumeProdusLaVanzare(String numeProdusLaVanzare) {
        this.numeProdusLaVanzare = numeProdusLaVanzare;
    }
}