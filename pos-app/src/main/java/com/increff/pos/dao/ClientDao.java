package com.increff.pos.dao;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ClientDao extends AbstractDao<ClientPojo> {

    private static final String SELECT_BY_NAME = "SELECT c FROM ClientPojo c WHERE LOWER(c.name) = :name";
    private static final String SEARCH_BY_QUERY = "SELECT c FROM ClientPojo c WHERE LOWER(c.name) LIKE :pattern ORDER BY c.name ASC";
    private static final String COUNT_BY_QUERY = "SELECT COUNT(c) FROM ClientPojo c WHERE LOWER(c.name) LIKE :pattern";
    private static final String SELECT_ALL_ORDERED = "SELECT c FROM ClientPojo c ORDER BY c.name ASC";

    public ClientDao() {
        super(ClientPojo.class);
    }

    public ClientPojo getClientByName(String clientName) {
        List<ClientPojo> result = em.createQuery(SELECT_BY_NAME, ClientPojo.class).setParameter("name", clientName.toLowerCase()).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<ClientPojo> getAllPaged(int page, int pageSize) {
        return runPagedQuery(SELECT_ALL_ORDERED, page, pageSize, null);
    }

    public List<ClientPojo> searchByQuery(String query, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("pattern", "%" + query.toLowerCase() + "%");
        return runPagedQuery(SEARCH_BY_QUERY, page, pageSize, params);
    }

    public long countByQuery(String query) {
        return em.createQuery(COUNT_BY_QUERY, Long.class).setParameter("pattern", "%" + query.toLowerCase() + "%").getSingleResult();
    }

    private List<ClientPojo> runPagedQuery(String jpql, int page, int pageSize, Map<String, Object> params) {
        return getPaginatedResults(jpql, page, pageSize, params);
    }
}
