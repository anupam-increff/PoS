package com.increff.pos.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

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
    
    public long countAll() {
        return em.createQuery("SELECT COUNT(*) FROM " + clazz.getSimpleName(), Long.class).getSingleResult();
    }

    protected List<T> getPaginatedResults(String jpql, int page, int pageSize, Map<String, Object> params) {
        javax.persistence.TypedQuery<T> query = em.createQuery(jpql, clazz);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query.setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
    protected String escapeLikePattern(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
