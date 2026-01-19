package org.example.model;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;


public class ProdusRepository {
    public ProdusRepository() {
        // use JPAUtil
    }

    public void salveaza(Produs produs)
    {
        EntityManager em = JPAUtil.getEntityManager();
        try
        {
            em.getTransaction().begin();
            em.persist(produs);
            em.getTransaction().commit();
        }
        finally
        {
            em.close();
        }
    }

    // New: update (merge) - returnează instanța managed
    public Produs update(Produs produs) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Produs merged = em.merge(produs);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    // New: delete
    public void sterge(Produs produs) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Produs managed = em.contains(produs) ? produs : em.merge(produs);
            em.remove(managed);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // New: find by id
    public Produs findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Produs.class, id);
        } finally {
            em.close();
        }
    }


    public List<Produs> gasesteTot() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            System.out.println("ProdusRepository: Se încarcă produsele... (așteptare 2 secunde)");
            Thread.sleep(2000); // SIMULARE ÎNTÂRZIERE
            return em.createQuery("SELECT p FROM Produs p", Produs.class).getResultList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public void close()
    {
        // JPAUtil handles EMF lifecycle
    }
}
