package org.example.model;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ComandaRepository {

    public ComandaRepository() {
    }

    public Comanda save(Comanda comanda) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Comanda savedComanda = em.merge(comanda);
            em.getTransaction().commit();
            return savedComanda;
        } finally {
            em.close();
        }
    }

    public List<Comanda> findByUser(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Comanda> query = em.createQuery("SELECT c FROM Comanda c LEFT JOIN FETCH c.user WHERE c.user.id = :userId", Comanda.class);
            query.setParameter("userId", user.getId());
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Comanda> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Comanda> q = em.createQuery("SELECT DISTINCT c FROM Comanda c LEFT JOIN FETCH c.user", Comanda.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public void close() {
    }
}