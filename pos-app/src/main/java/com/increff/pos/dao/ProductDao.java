package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ProductDao {

    @PersistenceContext
    private EntityManager em;

    public void insert(ProductPojo p) {
        em.persist(p);
    }

    public ProductPojo select(Integer id) {
        return em.find(ProductPojo.class, id);
    }

    public ProductPojo selectByBarcode(String barcode) {
        List<ProductPojo> list = em.createQuery("SELECT p FROM ProductPojo p WHERE p.barcode = :barcode", ProductPojo.class)
                .setParameter("barcode", barcode)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<ProductPojo> selectByClientId(Integer clientId) {
        return em.createQuery("SELECT p FROM ProductPojo p WHERE p.clientId = :clientId", ProductPojo.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    public List<ProductPojo> selectByName(String name) {
        return em.createQuery("SELECT p FROM ProductPojo p WHERE LOWER(p.name) LIKE :name", ProductPojo.class)
                .setParameter("name", "%" + name.toLowerCase() + "%")
                .getResultList();
    }

    public List<ProductPojo> selectAll() {
        return em.createQuery("FROM ProductPojo", ProductPojo.class).getResultList();
    }

    public void update(ProductPojo p) {
        em.merge(p);
    }

    public void delete(ProductPojo p) {
        em.remove(p);
    }
}

