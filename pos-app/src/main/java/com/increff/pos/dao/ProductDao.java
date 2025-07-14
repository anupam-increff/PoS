package com.increff.pos.dao;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(rollbackFor = ApiException.class)
public class ProductDao extends AbstractDao<ProductPojo> {

    private static final String SELECT_ALL = "SELECT p FROM ProductPojo p ORDER BY p.name ASC";
    private static final String COUNT_BY_CLIENT_ID = "SELECT COUNT(p) FROM ProductPojo p WHERE p.clientId = :clientId";
    private static final String SELECT_BY_BARCODE = "SELECT p FROM ProductPojo p WHERE p.barcode = :barcode";
    private static final String SEARCH_BY_BARCODE_OR_NAME = "SELECT p FROM ProductPojo p WHERE LOWER(p.barcode) LIKE :pattern OR LOWER(p.name) LIKE :pattern ORDER BY p.barcode ASC";
    private static final String COUNT_SEARCH_BY_BARCODE_OR_NAME = "SELECT COUNT(p) FROM ProductPojo p WHERE LOWER(p.barcode) LIKE :pattern OR LOWER(p.name) LIKE :pattern";
    private static final String SELECT_BY_CLIENT_ID = "SELECT p FROM ProductPojo p WHERE p.clientId = :clientId ORDER BY p.name ASC";

    public ProductDao() {
        super(ProductPojo.class);
    }

    public ProductPojo getByBarcode(String barcode) {
        List<ProductPojo> list = em.createQuery(SELECT_BY_BARCODE, ProductPojo.class).setParameter("barcode", barcode).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<ProductPojo> getAllPaged(int page, int pageSize) {
        return getPaginatedResults(SELECT_ALL, page, pageSize, null);
    }

    public List<ProductPojo> getByClientIdPaged(Integer clientId, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", clientId);
        return getPaginatedResults(SELECT_BY_CLIENT_ID, page, pageSize, params);
    }

    public long countByClientId(Integer clientId) {
        TypedQuery<Long> query = em.createQuery(COUNT_BY_CLIENT_ID, Long.class);
        query.setParameter("clientId", clientId);
        return query.getSingleResult();
    }

    public List<ProductPojo> searchByBarcode(String searchTerm, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("pattern", "%" + escapeLikePattern(searchTerm.toLowerCase()) + "%");
        return getPaginatedResults(SEARCH_BY_BARCODE_OR_NAME, page, pageSize, params);
    }

    public long countByBarcodeSearch(String searchTerm) {
        return em.createQuery(COUNT_SEARCH_BY_BARCODE_OR_NAME, Long.class).
                setParameter("pattern", "%" + escapeLikePattern(searchTerm.toLowerCase()) + "%").
                getSingleResult();
    }

    /**
     * Escapes SQL wildcards in user input to prevent SQL injection in LIKE queries
     * @param input The user input string
     * @return The escaped string safe for use in LIKE queries
     */
    private String escapeLikePattern(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\\", "\\\\")
                   .replace("%", "\\%")
                   .replace("_", "\\_");
    }
}
