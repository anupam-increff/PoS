package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(rollbackFor = ApiException.class)
public class OrderFlow {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InvoiceService invoiceService;

    public OrderData placeOrder(List<OrderItemPojo> orderItemPojos) {
        validateOrderItemsAndReduceInventory(orderItemPojos);
        Double total = calculateOrderTotalCost(orderItemPojos);
        Integer orderId = orderService.createOrderWithItems(orderItemPojos, total);
        OrderPojo orderPojo = orderService.getCheckByOrderId(orderId);
        return pojoToData(orderPojo);
    }

    public PaginatedResponse<OrderData> searchOrders(ZonedDateTime startDate, ZonedDateTime endDate, String query, int page, int size) {
        List<OrderPojo> pojos = orderService.search(startDate, endDate, query, page, size);
        long totalItems = orderService.countMatching(startDate, endDate, query);
        int totalPages = (int) Math.ceil((double) totalItems / size);
        List<OrderData> dataList = pojos.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, size);
    }

    public PaginatedResponse<OrderData> getAllPaginated(int page, int size) {
        List<OrderPojo> all = orderService.getAllPaginated(page, size);
        long total = orderService.countAll();
        int totalPages = (int) Math.ceil((double) total / size);
        List<OrderData> data = all.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(data, page, totalPages, total, size);
    }

    public List<OrderItemPojo> getOrderItemsByOrderId(Integer orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
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

    private Double calculateOrderTotalCost(List<OrderItemPojo> orderItemPojos) {
        return orderItemPojos.stream()
                .mapToDouble(i -> i.getSellingPrice() * i.getQuantity()).sum();
    }

    private OrderData pojoToData(OrderPojo pojo) {
        OrderData orderData = ConvertUtil.convert(pojo, OrderData.class);
        orderData.setPlacedAt(pojo.getCreatedAt());
        orderData.setInvoiceGenerated(invoiceService.getInvoiceStatus(pojo.getId()));
        orderData.setInvoiceId(invoiceService.getInvoiceIdByOrderId(pojo.getId()));
        return orderData;
    }
}
