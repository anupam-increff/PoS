package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class InventoryDao extends AbstractDao<InventoryPojo> {

    public InventoryDao() {
        super(InventoryPojo.class);
    }

    private static final String SELECT_BY_PRODUCT_ID = "SELECT i FROM InventoryPojo i WHERE i.productId = :pid";
    private static final String SEARCH_BY_BARCODE = "SELECT i FROM InventoryPojo i JOIN ProductPojo p ON i.productId = p.id WHERE LOWER(p.barcode) LIKE :barcode";
    private static final String COUNT_BY_BARCODE = "SELECT COUNT(i) FROM InventoryPojo i JOIN ProductPojo p ON i.productId = p.id WHERE LOWER(p.barcode) LIKE :barcode";

    public InventoryPojo getByProductId(Integer productId) {
        List<InventoryPojo> list = em.createQuery(SELECT_BY_PRODUCT_ID, InventoryPojo.class)
                .setParameter("pid", productId)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<InventoryPojo> getAllPaginated(int page, int pageSize) {
        return em.createQuery("SELECT i FROM InventoryPojo i", InventoryPojo.class)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("SELECT COUNT(i) FROM InventoryPojo i", Long.class)
                .getSingleResult();
    }

    public List<InventoryPojo> searchByBarcode(String barcode, int page, int pageSize) {
        return em.createQuery(SEARCH_BY_BARCODE, InventoryPojo.class)
                .setParameter("barcode", "%" + barcode.toLowerCase() + "%")
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countByBarcodeSearch(String barcode) {
        return em.createQuery(COUNT_BY_BARCODE, Long.class)
                .setParameter("barcode", "%" + barcode.toLowerCase() + "%")
                .getSingleResult();
    }
}
