package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class OrderItemDao {

    @PersistenceContext
    private EntityManager em;

    public void insert(OrderItemPojo p) {
        em.persist(p);
    }

    public List<OrderItemPojo> selectByOrderId(Integer orderId) {
        return em.createQuery("SELECT p FROM OrderItemPojo p WHERE p.orderId = :orderId", OrderItemPojo.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
