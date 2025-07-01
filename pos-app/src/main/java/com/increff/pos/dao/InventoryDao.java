package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class InventoryDao {
    @PersistenceContext
    private EntityManager em;

    public void insert(InventoryPojo pojo) {
        em.persist(pojo);
    }

    public void update(InventoryPojo pojo) {
        em.merge(pojo);
    }

    public InventoryPojo select(Integer id) {
        return em.find(InventoryPojo.class, id);
    }

    public InventoryPojo selectByProductId(Integer productId) {
        List<InventoryPojo> list = em.createQuery("SELECT i FROM InventoryPojo i WHERE i.productId = :pid", InventoryPojo.class)
                .setParameter("pid", productId)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<InventoryPojo> selectAll() {
        return em.createQuery("FROM InventoryPojo", InventoryPojo.class).getResultList();
    }
}
