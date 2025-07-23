package com.increff.pos.dao;

import com.increff.pos.pojo.ClientPojo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
//todo : since mysql is case insensitive we can remove LOWER things while comparisons where clause in every dao
public class ClientDao extends AbstractDao<ClientPojo> {

    private static final String SELECT_BY_NAME = "SELECT c FROM ClientPojo c WHERE LOWER(c.name) = :name";
    private static final String SEARCH_BY_QUERY = "SELECT c FROM ClientPojo c WHERE LOWER(c.name) LIKE :pattern ORDER BY c.name ASC";
    private static final String COUNT_BY_QUERY = "SELECT COUNT(c) FROM ClientPojo c WHERE LOWER(c.name) LIKE :pattern";
    private static final String SELECT_ALL_ORDERED = "SELECT c FROM ClientPojo c ORDER BY c.name ASC";

    public ClientDao() {
        super(ClientPojo.class);
    }

    public ClientPojo getClientByName(String clientName) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", clientName.toLowerCase());
        return getSingleResultOrNull(SELECT_BY_NAME, params);
    }

    public List<ClientPojo> getAllClients(int page, int pageSize) {
        return getPaginatedResults(SELECT_ALL_ORDERED, page, pageSize, null);
    }

    public List<ClientPojo> searchClientByName(String query, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("pattern", toLikePattern(query));
        return getPaginatedResults(SEARCH_BY_QUERY, page, pageSize, params);
    }

    public long countByQuery(String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("pattern", toLikePattern(query));
        return getCount(COUNT_BY_QUERY, params);
    }
}
