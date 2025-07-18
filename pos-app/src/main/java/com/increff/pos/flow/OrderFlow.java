package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderSearchForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(rollbackFor = ApiException.class)
public class OrderFlow {

    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    public Integer placeOrder(OrderForm orderForm) {
        List<OrderItemPojo> orderItemPojos = createOrderItems(orderForm);
        double total = calculateTotal(orderItemPojos);
        OrderPojo order = createOrder(total);
        Integer orderId = orderService.createOrder(order);
        saveOrderItems(orderItemPojos, orderId);
        return orderId;
    }

    public PaginatedResponse<OrderData> searchOrders(ZonedDateTime startDate, ZonedDateTime endDate, Boolean invoiceGenerated, String query, int page, int size) {
        List<OrderPojo> pojos = orderService.search(startDate, endDate, invoiceGenerated, query, page, size);
        long totalItems = orderService.countMatching(startDate, endDate, invoiceGenerated, query);
        int totalPages = (int) Math.ceil((double) totalItems / size);
        List<OrderData> dataList = pojos.stream().map(this::convert).collect(Collectors.toList());
        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, size);
    }

    public PaginatedResponse<OrderData> getAllPaginated(int page, int size) {
        List<OrderPojo> all = orderService.getAllPaginated(page, size);
        long total = orderService.countAll();
        int totalPages = (int) Math.ceil((double) total / size);
        List<OrderData> data = all.stream().map(this::convert).collect(Collectors.toList());
        return new PaginatedResponse<>(data, page, totalPages, total, size);
    }

    public List<OrderItemData> getOrderItemsByOrderId(Integer orderId) {
        return orderItemService.getByOrderId(orderId).stream()
                .map(this::convertToOrderItemData)
                .collect(Collectors.toList());
    }

    private List<OrderItemPojo> createOrderItems(OrderForm orderForm) {
        return orderForm.getItems().stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());
    }

    private OrderItemPojo createOrderItem(OrderItemForm form) {
        ProductPojo product = productService.getCheckProductByBarcode(form.getBarcode());
        validateSellingPrice(form.getSellingPrice(), product);
        InventoryPojo inventory = inventoryService.getCheckByProductId(product.getId());

        validateInventory(inventory, form.getQuantity(), product.getName());
        inventory.setQuantity(inventory.getQuantity() - form.getQuantity());

        return buildOrderItem(product.getId(), form);
    }

    private void validateInventory(InventoryPojo inventory, Integer quantity, String productName) {
        if (inventory.getQuantity() < quantity) {
            throw new ApiException("Insufficient inventory for: " + productName);
        }
    }

    private void validateSellingPrice(Double sellingPrice, ProductPojo product) {
        if (sellingPrice > product.getMrp()) {
            throw new ApiException("Selling price for product " + product.getName() + " cannot be greater than its mrp " + product.getMrp());
        }
    }


    private OrderItemPojo buildOrderItem(Integer productId, OrderItemForm form) {
        OrderItemPojo pojo = new OrderItemPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(form.getQuantity());
        pojo.setSellingPrice(form.getSellingPrice());
        return pojo;
    }

    private double calculateTotal(List<OrderItemPojo> orderItemPojos) {
        return orderItemPojos.stream()
                .mapToDouble(i -> i.getSellingPrice() * i.getQuantity()).sum();
    }

    private OrderPojo createOrder(Double total) {
        OrderPojo order = new OrderPojo();
        order.setPlacedAt(ZonedDateTime.now());
        order.setInvoiceGenerated(false);
        order.setTotal(total);
        return order;
    }

    private void saveOrderItems(List<OrderItemPojo> orderItemPojos, Integer orderId) {
        for (OrderItemPojo item : orderItemPojos) {
            item.setOrderId(orderId);
            orderItemService.add(item);
        }
    }

    private OrderItemData convertToOrderItemData(OrderItemPojo item) {
        ProductPojo product = productService.getCheckProductById(item.getProductId());
        OrderItemData data = ConvertUtil.convert(item, OrderItemData.class);
        data.setBarcode(product.getBarcode());
        data.setProductName(product.getName());
        return data;
    }

    private OrderData convert(OrderPojo pojo) {
        return ConvertUtil.convert(pojo, OrderData.class);
    }
}
