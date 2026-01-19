package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "comanda_items")
public class ComandaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Un item se referă la un singur Produs din meniu.
    // FetchType.EAGER: Când încărcăm un item, vrem să vină și produsul asociat cu el.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produs_id", nullable = true)
    private Produs produs;

    private int cantitate;

    // Stocăm prețul la momentul vânzării pentru a nu fi afectați de schimbări viitoare ale prețului în meniu
    private double pretLaVanzare;

    private String numeProdusLaVanzare; // Stocăm și numele, util pentru afișare pe bonuri vechi

    // Un item aparține unei singure Comenzi.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;

    // Constructori
    public ComandaItem() {}

    public ComandaItem(Produs produs, int cantitate, Comanda comanda) {
        this.produs = produs;
        this.numeProdusLaVanzare = produs != null ? produs.getNume() : "";
        this.cantitate = cantitate;
        this.pretLaVanzare = produs != null ? produs.getPret() : 0.0;
        this.comanda = comanda;
    }

    // Getteri și Setteri
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