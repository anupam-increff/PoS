package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao itemDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;

    @Transactional
    public Integer createOrder(List<OrderItemPojo> items) {
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        orderDao.insert(order);

        for (OrderItemPojo item : items) {
            ProductPojo product = productDao.select(item.getProductId());
            if (product == null) throw new ApiException("Product not found");

            InventoryPojo inv = inventoryDao.selectByProductId(item.getProductId());
            if (inv.getQuantity() < item.getQuantity()) {
                throw new ApiException("Insufficient inventory for product " + product.getName());
            }
            inv.setQuantity(inv.getQuantity() - item.getQuantity());

            item.setOrderId(order.getId());
            itemDao.insert(item);
        }
        return order.getId();
    }

    public List<OrderPojo> getAll() {
        return orderDao.selectAll();
    }

    public List<OrderItemPojo> getOrderItems(Integer orderId) {
        return itemDao.selectByOrderId(orderId);
    }
}
