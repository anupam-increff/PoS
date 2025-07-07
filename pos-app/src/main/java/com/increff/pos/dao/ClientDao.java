package com.increff.pos.dao;

import com.increff.pos.pojo.ClientPojo;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ClientDao extends AbstractDao<ClientPojo> {

    private static final String SELECT_BY_NAME = "SELECT c FROM ClientPojo c WHERE LOWER(c.name) = :name";
    private static final String COUNT_ALL = "SELECT COUNT(c) FROM ClientPojo c";
    private static final String SEARCH_BY_QUERY = "SELECT c FROM ClientPojo c WHERE LOWER(c.name) LIKE :pattern ORDER BY c.name ASC";
    private static final String COUNT_BY_QUERY = "SELECT COUNT(c) FROM ClientPojo c WHERE LOWER(c.name) LIKE :pattern";

    public ClientDao() {
        super(ClientPojo.class);
    }

    public ClientPojo getClientByName(String clientName) {
        return em.createQuery(SELECT_BY_NAME, ClientPojo.class)
                .setParameter("name", clientName.toLowerCase())
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public long countAll() {
        return em.createQuery(COUNT_ALL, Long.class).getSingleResult();
    }

    public List<ClientPojo> getAllPaged(int page, int pageSize) {
        String jpql = "SELECT c FROM ClientPojo c ORDER BY c.name ASC";
        return em.createQuery(jpql, ClientPojo.class)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countByQuery(String query) {
        return em.createQuery(COUNT_BY_QUERY, Long.class)
                .setParameter("pattern", "%" + query.toLowerCase() + "%")
                .getSingleResult();
    }

    public List<ClientPojo> searchByQuery(String query, int page, int pageSize) {
        return em.createQuery(SEARCH_BY_QUERY, ClientPojo.class)
                .setParameter("pattern", "%" + query.toLowerCase() + "%")
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
}
