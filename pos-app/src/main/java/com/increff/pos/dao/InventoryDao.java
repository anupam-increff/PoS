package com.increff.pos.dao;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(rollbackFor = ApiException.class)
public class InventoryDao extends AbstractDao<InventoryPojo> {

    private static final String SELECT_BY_PRODUCT_ID = "SELECT i FROM InventoryPojo i WHERE i.productId = :pid";
    private static final String SELECT_ALL = "SELECT i FROM InventoryPojo i ORDER BY i.quantity DESC";
    private static final String SEARCH_BY_BARCODE_OR_NAME = "SELECT i FROM InventoryPojo i JOIN ProductPojo p ON i.productId = p.id WHERE LOWER(p.barcode) LIKE :searchTerm OR LOWER(p.name) LIKE :searchTerm";
    private static final String COUNT_BY_BARCODE_OR_NAME = "SELECT COUNT(i) FROM InventoryPojo i JOIN ProductPojo p ON i.productId = p.id WHERE LOWER(p.barcode) LIKE :searchTerm OR LOWER(p.name) LIKE :searchTerm";

    public InventoryDao() {
        super(InventoryPojo.class);
    }

    public InventoryPojo getByProductId(Integer productId) {
        List<InventoryPojo> list = em.createQuery(SELECT_BY_PRODUCT_ID, InventoryPojo.class).setParameter("pid", productId).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<InventoryPojo> getAllPaginated(int page, int pageSize) {
        return getPaginatedResults(SELECT_ALL, page, pageSize, null);
    }

    public List<InventoryPojo> searchByBarcode(String searchTerm, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("searchTerm", "%" + escapeLikePattern(searchTerm.toLowerCase()) + "%");
        return getPaginatedResults(SEARCH_BY_BARCODE_OR_NAME, page, pageSize, params);
    }

    public long countByBarcodeSearch(String searchTerm) {
        return em.createQuery(COUNT_BY_BARCODE_OR_NAME, Long.class).setParameter("searchTerm", "%" + escapeLikePattern(searchTerm.toLowerCase()) + "%").getSingleResult();
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
