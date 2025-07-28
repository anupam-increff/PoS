package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = ApiException.class)
public class OrderFlow {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

    public OrderPojo placeOrder(List<OrderItemPojo> orderItemPojos) {
        for (OrderItemPojo item : orderItemPojos) {
            ProductPojo product = productService.getCheckProductById(item.getProductId());
            productService.validateSellingPrice(item.getSellingPrice(), product);
            inventoryService.validateSufficientAndReduceInventory(product.getId(), item.getQuantity(), product.getName());
        }
        Integer orderId = orderService.createOrderWithItems(orderItemPojos);
        return orderService.getCheckByOrderId(orderId);
    }

}
