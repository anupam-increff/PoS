package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
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
        validateOrderItemsAndReduceInventory(orderItemPojos);
        Integer orderId = orderService.createOrderWithItems(orderItemPojos);
        return orderService.getCheckByOrderId(orderId);
    }

    public List<OrderPojo> searchOrders(ZonedDateTime startDate, ZonedDateTime endDate, String query, int page, int size) {
        return orderService.searchOrderByQuery(startDate, endDate, query, page, size);
    }

    public long countMatchingOrders(ZonedDateTime startDate, ZonedDateTime endDate, String query) {
        return orderService.countMatchingOrdersByQuery(startDate, endDate, query);
    }

    public List<OrderPojo> getAllOrders(int page, int size) {
        return orderService.getAllOrdersPaginated(page, size);
    }

    public List<OrderItemPojo> getOrderItemsByOrderId(Integer orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
    }

    public long countAllOrders() {
        return orderService.countAll();
    }

    private void validateOrderItemsAndReduceInventory(List<OrderItemPojo> orderItemPojos) {
        for (OrderItemPojo item : orderItemPojos) {
            ProductPojo product = productService.getCheckProductById(item.getProductId());
            validateSellingPriceAgainstMrp(item.getSellingPrice(), product);
            InventoryPojo inventory = inventoryService.getCheckByProductId(product.getId());
            validateSufficientInventory(inventory, item.getQuantity(), product.getName());
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
        }
    }

    private void validateSufficientInventory(InventoryPojo inventory, Integer quantity, String productName) {
        if (inventory.getQuantity() < quantity) {
            throw new ApiException("Insufficient inventory for: " + productName);
        }
    }

    private void validateSellingPriceAgainstMrp(Double sellingPrice, ProductPojo product) {
        if (sellingPrice > product.getMrp()) {
            throw new ApiException("Selling price for product " + product.getName() + " cannot be greater than its mrp " + product.getMrp());
        }
    }
}
