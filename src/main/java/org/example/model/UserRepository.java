package org.example.model;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    public UserRepository() {
    }

    public void save(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public User update(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User updatedUser = em.merge(user);
            em.getTransaction().commit();
            return updatedUser;
        } finally {
            em.close();
        }
    }

    public void delete(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Trebuie să ne asigurăm că entitatea este "atașată" înainte de a o șterge
            if (!em.contains(user)) {
                user = em.merge(user);
            }
            em.remove(user); // Șterge user-ul (și comenzile lui, datorită CascadeType.ALL)
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Optional<User> findByUsername(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty(); // Returnează un Optional gol dacă nu găsește user-ul
        } finally {
            em.close();
        }
    }

    public List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            System.out.println("UserRepository: Se încarcă userii... (așteptare 2 secunde)");
            Thread.sleep(2000); // SIMULARE ÎNTÂRZIERE
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public void close() {
        // Nothing to close here — JPAUtil manages the factory lifecycle
    }
}