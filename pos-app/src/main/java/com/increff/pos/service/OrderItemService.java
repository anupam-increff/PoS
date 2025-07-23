package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
//todo : move into order service
@Service
@Transactional(rollbackFor = ApiException.class)
public class OrderItemService {

    @Autowired
    private OrderItemDao itemDao;

    public void add(OrderItemPojo orderItemPojo) {
        itemDao.insert(orderItemPojo);
    }

    public List<OrderItemPojo> getByOrderId(Integer orderId) {
        return itemDao.getByOrderId(orderId);
    }
}

