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

    private static final String SELECT_BY_BARCODE = "SELECT p FROM ProductPojo p WHERE p.barcode = :barcode";
    private static final String SELECT_BY_CLIENT_ID = "SELECT p FROM ProductPojo p WHERE p.clientId = :clientId";
    private static final String SELECT_BY_NAME = "SELECT p FROM ProductPojo p WHERE LOWER(p.name) LIKE :name";

    public ProductPojo getByBarcode(String barcode) {
        return em.createQuery(SELECT_BY_BARCODE, ProductPojo.class)
                .setParameter("barcode", barcode)
                        .getResultList()
                        .stream()
                        .findFirst()
                        .orElse(null);
    }

    public List<ProductPojo> getByClientId(Integer clientId) {
        return em.createQuery(SELECT_BY_CLIENT_ID, ProductPojo.class)
                .setParameter("clientId", clientId)
                        .getResultList();
    }

    public List<ProductPojo> getByName(String name) {
        return em.createQuery(SELECT_BY_NAME, ProductPojo.class)
                .setParameter("name", "%" + name.toLowerCase() + "%")
                        .getResultList();
    }
}
