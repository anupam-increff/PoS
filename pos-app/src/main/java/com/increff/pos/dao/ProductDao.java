package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ProductDao extends AbstractDao<ProductPojo> {

    public ProductDao() {
        super(ProductPojo.class);
    }

    private static final String SELECT_ALL = "SELECT p FROM ProductPojo p ORDER BY p.name ASC";
    private static final String COUNT_ALL = "SELECT COUNT(p) FROM ProductPojo p";
    private static final String COUNT_BY_CLIENT_ID = "SELECT COUNT(p) FROM ProductPojo p WHERE p.clientId = :clientId";
    private static final String SELECT_BY_BARCODE = "SELECT p FROM ProductPojo p WHERE p.barcode = :barcode";
    private static final String SEARCH_BY_BARCODE = "SELECT p FROM ProductPojo p WHERE LOWER(p.barcode) LIKE :pattern ORDER BY p.barcode ASC";
    private static final String COUNT_SEARCH_BY_BARCODE = "SELECT COUNT(p) FROM ProductPojo p WHERE LOWER(p.barcode) LIKE :pattern";
    private static final String SELECT_BY_CLIENT_ID = "SELECT p FROM ProductPojo p WHERE p.clientId = :clientId ORDER BY p.name ASC";

    public ProductPojo getByBarcode(String barcode) {
        return em.createQuery(SELECT_BY_BARCODE, ProductPojo.class)
                .setParameter("barcode", barcode)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<ProductPojo> getAllPaged(int page, int pageSize) {
        return getPaginatedResults(SELECT_ALL, page, pageSize);
    }

    public long countAll() {
        return em.createQuery(COUNT_ALL, Long.class).getSingleResult();
    }

    public List<ProductPojo> getByClientIdPaged(Integer clientId, int page, int pageSize) {
        return em.createQuery(SELECT_BY_CLIENT_ID, ProductPojo.class)
                .setParameter("clientId", clientId)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countByClientId(Integer clientId) {
        return em.createQuery(COUNT_BY_CLIENT_ID, Long.class)
                .setParameter("clientId", clientId)
                .getSingleResult();
    }

    public List<ProductPojo> searchByBarcode(String barcode, int page, int pageSize) {
        return em.createQuery(SEARCH_BY_BARCODE, ProductPojo.class)
                .setParameter("pattern", "%" + barcode.toLowerCase() + "%")
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countByBarcodeSearch(String barcode) {
        return em.createQuery(COUNT_SEARCH_BY_BARCODE, Long.class)
                .setParameter("pattern", "%" + barcode.toLowerCase() + "%")
                .getSingleResult();
    }
}
