package com.increff.pos.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

public abstract class AbstractDao<T> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> entityClass;

    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void insert(T entity) {
        em.persist(entity);
    }

    public T getById(Integer id) {
        return em.find(entityClass, id);
    }

    public List<T> getAll() {
        return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName(), Long.class)
                .getSingleResult();
    }

    protected T getSingleResult(String jpql, Map<String, Object> params) {
        try {
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            setParameters(query, params);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    protected List<T> getPaginatedResults(String jpql, int page, int pageSize, Map<String, Object> params) {
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        setParameters(query, params);
        return query.setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    protected long getCount(String jpql, Map<String, Object> params) {
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        setParameters(query, params);
        return query.getSingleResult();
    }

    private void setParameters(TypedQuery<?> query, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    protected String escapeLikePattern(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    protected String toLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return "%";
        }
        return "%" + escapeLikePattern(searchTerm.trim().toLowerCase()) + "%";
    }

}
