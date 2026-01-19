package org.example.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // Folosim "users" pentru că "user" e adesea un cuvânt cheie în SQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Stocăm rolul ca text ("STAFF", "ADMIN")
    @Column(nullable = false)
    private UserRole rol;

    // Un User (ospătar) poate avea mai multe Comenzi.
    // CascadeType.ALL: Dacă ștergem un User, ștergem automat și toate comenzile lui.
    // orphanRemoval=true: Dacă scoatem o comandă din lista asta, ea va fi ștearsă din DB.
    // mappedBy: Spune JPA că relația este "deținută" de câmpul 'user' din clasa Comanda.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comanda> comenzi = new ArrayList<>();

    // Constructori
    public User() {}

    public User(String username, String password, UserRole rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    // Getteri și Setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRol() {
        return rol;
    }

    public void setRol(UserRole rol) {
        this.rol = rol;
    }

    public List<Comanda> getComenzi() {
        return comenzi;
    }

    public void setComenzi(List<Comanda> comenzi) {
        this.comenzi = comenzi;
    }

    @Override
    public String toString() {
        return username; // Util pentru afișare în tabele
    }
}