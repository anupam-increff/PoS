package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
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
        Map<String, Object> params = new HashMap<>();
        params.put("barcode", barcode);
        return getSingleResult(SELECT_BY_BARCODE, params);
    }

    public List<ProductPojo> getAllProducts(int page, int pageSize) {
        return getPaginatedResults(SELECT_ALL, page, pageSize, null);
    }

    public List<ProductPojo> getProductsByClientId(Integer clientId, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", clientId);
        return getPaginatedResults(SELECT_BY_CLIENT_ID, page, pageSize, params);
    }

    public long countByClientId(Integer clientId) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", clientId);
        return getCount(COUNT_BY_CLIENT_ID, params);
    }

    public List<ProductPojo> searchByBarcode(String searchTerm, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("pattern", toLikePattern(searchTerm));
        return getPaginatedResults(SEARCH_BY_BARCODE_OR_NAME, page, pageSize, params);
    }

    public long countByBarcodeSearch(String searchTerm) {
        Map<String, Object> params = new HashMap<>();
        params.put("pattern", toLikePattern(searchTerm));
        return getCount(COUNT_SEARCH_BY_BARCODE_OR_NAME, params);
    }
}
