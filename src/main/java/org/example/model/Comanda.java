package org.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comenzi")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime dataPlasare;
    private int numarMasa;
    private double subtotal;
    private double totalReduceri;
    private double totalFinal;
    private boolean finalizata = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ComandaItem> items = new ArrayList<>();

    public Comanda() {
        this.dataPlasare = LocalDateTime.now();
    }

    public Comanda(int numarMasa, User user) {
        this();
        this.numarMasa = numarMasa;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataPlasare() {
        return dataPlasare;
    }

    public void setDataPlasare(LocalDateTime dataPlasare) {
        this.dataPlasare = dataPlasare;
    }

    public int getNumarMasa() {
        return numarMasa;
    }

    public void setNumarMasa(int numarMasa) {
        this.numarMasa = numarMasa;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotalReduceri() {
        return totalReduceri;
    }

    public void setTotalReduceri(double totalReduceri) {
        this.totalReduceri = totalReduceri;
    }

    public double getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(double totalFinal) {
        this.totalFinal = totalFinal;
    }

    public boolean isFinalizata() {
        return finalizata;
    }

    public void setFinalizata(boolean finalizata) {
        this.finalizata = finalizata;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ComandaItem> getItems() {
        return items;
    }

    public void setItems(List<ComandaItem> items) {
        this.items = items;
    }

    public void adaugaItem(Produs produs, int cantitate) {
        if (produs == null || cantitate <= 0) return;
        for (ComandaItem item : items) {
            if (item.getProdus().getId() == produs.getId()) {
                item.setCantitate(item.getCantitate() + cantitate);
                return;
            }
        }
        ComandaItem newItem = new ComandaItem(produs, cantitate, this);
        items.add(newItem);
    }

    public void calculeazaTotaluri() {
        double sub = 0.0;
        for (ComandaItem item : items) {
            sub += item.getPretLaVanzare() * item.getCantitate();
        }
        this.subtotal = sub;
        this.totalFinal = this.subtotal - this.totalReduceri;
    }
}