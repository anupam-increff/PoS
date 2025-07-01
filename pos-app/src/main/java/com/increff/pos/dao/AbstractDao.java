package com.increff.pos.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public abstract class AbstractDao<T> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> clazz;

    protected AbstractDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void insert(T entity) {
        em.persist(entity);
    }

    public T getById(Integer id) {
        return em.find(clazz, id);
    }

    public void update(T entity) {
        em.merge(entity);
    }

    public List<T> getAll() {
        return em.createQuery("FROM " + clazz.getSimpleName(), clazz).getResultList();
    }
}
