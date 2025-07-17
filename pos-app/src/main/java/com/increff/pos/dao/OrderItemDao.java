package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import com.increff.pos.exception.ApiException;
import java.util.List;

@Repository
@Transactional
public class OrderItemDao extends AbstractDao<OrderItemPojo> {

    public OrderItemDao() {
        super(OrderItemPojo.class);
    }
    private static final String GET_BY_ORDER_ID="SELECT p FROM OrderItemPojo p WHERE p.orderId = :orderId";

    public List<OrderItemPojo> getByOrderId(Integer orderId) {
        return em.createQuery(
                GET_BY_ORDER_ID, OrderItemPojo.class)
                        .setParameter("orderId", orderId)
                                .getResultList();
    }
}
