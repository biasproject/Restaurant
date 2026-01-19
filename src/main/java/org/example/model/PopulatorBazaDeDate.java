package org.example.model;

public class PopulatorBazaDeDate {

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        ProdusRepository produsRepository = new ProdusRepository();

        System.out.println("Începe popularea bazei de date...");

        try {
            System.out.println("Verificare și adăugare utilizatori...");

            if (userRepository.findByUsername("admin").isEmpty()) {
                User manager = new User("admin", "admin", UserRole.ADMIN);
                userRepository.save(manager);
                System.out.println("Utilizatorul 'admin' a fost creat.");
            } else {
                System.out.println("Utilizatorul 'admin' există deja.");
            }

            if (userRepository.findByUsername("staff").isEmpty()) {
                User ospatar = new User("staff", "staff", UserRole.STAFF);
                userRepository.save(ospatar);
                System.out.println("Utilizatorul 'staff' a fost creat.");
            } else {
                System.out.println("Utilizatorul 'staff' există deja.");
            }


            System.out.println("Adăugare produse în meniu (dacă nu există deja)...");

            produsRepository.salveaza(new Mancare("Hummus", 32.0f, "Aperitive", 250, true));


            System.out.println("-------------------------------------------");
            System.out.println("POPULARE FINALIZATĂ!");
            System.out.println("-------------------------------------------");

        } catch (Exception e) {
            System.err.println("A APĂRUT O EROARE ÎN TIMPUL POPULĂRII!");
            e.printStackTrace();
        } finally {
            userRepository.close();
            produsRepository.close();
        }
    }
}