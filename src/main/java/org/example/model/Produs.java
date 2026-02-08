package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "produse")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
public abstract class Produs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nume;
    private float pret;
    private String categorie;

    public Produs(String nume, float pret, String categorie) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
    }

    protected Produs() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public float getPret() { return pret; }
    public void setPret(float pret) { this.pret = pret; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }




    public void afisareDetalii() {
        System.out.println("Nume: " + nume + ", Pret: " + pret);
    }

    public abstract String obtineDetalii();

    @Override
    public String toString() {
        return this.getNume();
    }
}