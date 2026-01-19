package org.example.model;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * O clasă simplă, de test, pentru a verifica dacă fișierul persistence.xml
 * este corect și dacă ne putem conecta la baza de date.
 */
public class TestConexiuneDB {

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("Începe testul de conexiune la baza de date...");
        System.out.println("=====================================================");

        try {
            // Aceasta este singura linie importantă.
            // Încercăm să pornim "fabrica" de conexiuni folosind configurarea din persistence.xml.
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurant-pu");

            // Dacă linia de mai sus nu a aruncat o eroare, înseamnă că totul e BINE!
            System.out.println("\n");
            System.out.println("*****************************************************");
            System.out.println("************    SUCCES! Conexiune OK!    ************");
            System.out.println("*****************************************************");
            System.out.println("Hibernate a citit corect persistence.xml și s-a conectat la baza de date.");

            // Nu uita să închizi fabrica la final.
            emf.close();

        } catch (Exception e) {
            // Dacă ajungem aici, înseamnă că a apărut o eroare la conectare.
            System.err.println("\n");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!    EROARE CRITICĂ LA CONEXIUNE    !!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("Verifică URL-ul, numele bazei de date, user-ul și parola în 'persistence.xml'!");

            // Afișăm eroarea completă ca să vedem exact ce s-a întâmplat.
            e.printStackTrace();
        }

        System.out.println("\n=====================================================");
        System.out.println("Testul de conexiune s-a încheiat.");
        System.out.println("=====================================================");
    }
}