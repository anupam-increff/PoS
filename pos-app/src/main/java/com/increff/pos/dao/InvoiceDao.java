package com.increff.pos.dao;

import com.increff.pos.pojo.InvoicePojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class InvoiceDao extends AbstractDao<InvoicePojo> {

    private static final String SELECT_BY_ORDER = "SELECT i FROM InvoicePojo i WHERE i.orderId = :orderId";

    public InvoiceDao() {
        super(InvoicePojo.class);
    }

    public InvoicePojo getByOrderId(Integer orderId) {
        return em.createQuery(SELECT_BY_ORDER, InvoicePojo.class)
                .setParameter("orderId", orderId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
} 