package com.increff.pos.dao;

import com.increff.pos.pojo.InvoicePojo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InvoiceDao extends AbstractDao<InvoicePojo> {

    private static final String SELECT_BY_ORDER = "SELECT i FROM InvoicePojo i WHERE i.orderId = :orderId";
    private static final String SELECT_BY_ID = "SELECT i FROM InvoicePojo i WHERE i.id = :id";

    public InvoiceDao() {
        super(InvoicePojo.class);
    }

    public InvoicePojo getByOrderId(Integer orderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        return getSingleResultOrNull(SELECT_BY_ORDER, params);
    }

    public InvoicePojo getById(Integer id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return getSingleResultOrNull(SELECT_BY_ID, params);
    }
}