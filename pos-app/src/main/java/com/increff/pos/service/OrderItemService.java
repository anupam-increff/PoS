package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao itemDao;

    public List<OrderItemPojo> getByOrderId(Integer orderId) {
        return itemDao.getByOrderId(orderId);
    }
}

