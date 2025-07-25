package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InventoryDao extends AbstractDao<InventoryPojo> {

    private static final String SELECT_BY_PRODUCT_ID = "SELECT i FROM InventoryPojo i WHERE i.productId = :pid";
    private static final String SELECT_ALL = "SELECT i FROM InventoryPojo i ORDER BY i.quantity DESC";
    private static final String SEARCH_BY_BARCODE_OR_NAME = "SELECT i FROM InventoryPojo i JOIN ProductPojo p ON i.productId = p.id WHERE p.barcode LIKE :searchTerm OR p.name LIKE :searchTerm";
    private static final String COUNT_SEARCH_BY_BARCODE_OR_NAME = "SELECT COUNT(i) FROM InventoryPojo i JOIN ProductPojo p ON i.productId = p.id WHERE p.barcode LIKE :searchTerm OR p.name LIKE :searchTerm";

    public InventoryDao() {
        super(InventoryPojo.class);
    }

    public InventoryPojo getByProductId(Integer productId) {
        List<InventoryPojo> list = em.createQuery(SELECT_BY_PRODUCT_ID, InventoryPojo.class).
                setParameter("pid", productId).
                getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<InventoryPojo> getAllInventory(int page, int pageSize) {
        return getPaginatedResults(SELECT_ALL, page, pageSize, null);
    }

    public List<InventoryPojo> searchByBarcode(String searchTerm, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("searchTerm", "%" + escapeLikePattern(searchTerm) + "%");
        return getPaginatedResults(SEARCH_BY_BARCODE_OR_NAME, page, pageSize, params);
    }

    public long countByBarcodeSearch(String searchTerm) {
        return em.createQuery(COUNT_SEARCH_BY_BARCODE_OR_NAME, Long.class).
                setParameter("searchTerm", "%" + escapeLikePattern(searchTerm) + "%").
                getSingleResult();
    }

}
