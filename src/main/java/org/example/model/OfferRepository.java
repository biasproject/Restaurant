package org.example.model;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class OfferRepository {

    public OfferRepository() {
    }

    public OfferConfig save(OfferConfig offer) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(offer);
            em.getTransaction().commit();
            return offer;
        } finally {
            em.close();
        }
    }

    public OfferConfig update(OfferConfig offer) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            OfferConfig merged = em.merge(offer);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    public Optional<OfferConfig> findByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<OfferConfig> q = em.createQuery("SELECT o FROM OfferConfig o WHERE o.name = :name", OfferConfig.class);
            q.setParameter("name", name);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<OfferConfig> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<OfferConfig> q = em.createQuery("SELECT o FROM OfferConfig o", OfferConfig.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}

