package com.increff.pos.order.integration.dao;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class OrderItemDaoIntegrationTest extends AbstractTest {

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    private OrderPojo order;
    private ProductPojo product1;
    private ProductPojo product2;

    @Before
    public void setUp() {
        // Create test order
        order = new OrderPojo();
        order.setOrderStatus(OrderStatus.CREATED);
        orderDao.insert(order);

        // Create test products
        product1 = new ProductPojo();
        product1.setBarcode("TEST001");
        product1.setName("Test Product 1");
        product1.setMrp(100.0);
        product1.setClientId(1);
        productDao.insert(product1);

        product2 = new ProductPojo();
        product2.setBarcode("TEST002");
        product2.setName("Test Product 2");
        product2.setMrp(200.0);
        product2.setClientId(1);
        productDao.insert(product2);
    }

    @Test
    public void testInsert() {
        OrderItemPojo item = new OrderItemPojo();
        item.setOrderId(order.getId());
        item.setProductId(product1.getId());
        item.setQuantity(5);
        item.setSellingPrice(90.0);
        orderItemDao.insert(item);

        assertNotNull(item.getId());
    }

    @Test
    public void testGetByOrderId() {
        OrderItemPojo item1 = createOrderItem(product1.getId(), 5, 90.0);
        OrderItemPojo item2 = createOrderItem(product2.getId(), 3, 180.0);

        List<OrderItemPojo> items = orderItemDao.getByOrderId(order.getId());
        assertEquals(2, items.size());
    }

    @Test
    public void testGetByOrderIdNotFound() {
        List<OrderItemPojo> items = orderItemDao.getByOrderId(999);
        assertTrue(items.isEmpty());
    }

    private OrderItemPojo createOrderItem(Integer productId, Integer quantity, Double sellingPrice) {
        OrderItemPojo item = new OrderItemPojo();
        item.setOrderId(order.getId());
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setSellingPrice(sellingPrice);
        orderItemDao.insert(item);
        return item;
    }
} 