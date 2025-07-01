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
    private static final String SELECT_BY_PRODUCT_ID= "SELECT i FROM InventoryPojo i WHERE i.productId = :pid";

    public InventoryPojo getByProductId(Integer productId) {
        List<InventoryPojo> list = em.createQuery(
                SELECT_BY_PRODUCT_ID, InventoryPojo.class)
                        .setParameter("pid", productId)
                                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }
}
