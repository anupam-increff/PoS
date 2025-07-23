package com.increff.pos.flow;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(rollbackFor = ApiException.class)
public class InvoiceFlow {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;

    public byte[] getInvoice(Integer orderId) {
        return invoiceService.downloadInvoice(orderId);
    }

    public void generateInvoice(Integer orderId) {
        if (invoiceService.getInvoiceStatus(orderId)) {
            throw new ApiException("Invoice already exists for order ID: " + orderId);
        }

        OrderPojo order = orderService.getCheckByOrderId(orderId);
        List<OrderItemPojo> orderItems = orderService.getOrderItemsByOrderId(orderId);

        List<OrderItemData> itemDataList = buildOrderItemDataList(orderItems);
        double calculatedTotal = calculateOrderTotal(orderItems);
        OrderData orderData = buildOrderData(order, calculatedTotal);
        String invoicePath = generateInvoicePath(orderId);

        invoiceService.createInvoice(orderId, invoicePath, orderData, itemDataList);
    }

    private List<OrderItemData> buildOrderItemDataList(List<OrderItemPojo> orderItems) {
        List<OrderItemData> itemDataList = new ArrayList<>();

        for (OrderItemPojo item : orderItems) {
            ProductPojo product = productService.getCheckProductById(item.getProductId());
            OrderItemData data = createOrderItemData(item, product);
            itemDataList.add(data);
        }

        return itemDataList;
    }

    private OrderItemData createOrderItemData(OrderItemPojo orderItemPojo, ProductPojo product) {
        OrderItemData data = ConvertUtil.convert(orderItemPojo, OrderItemData.class);
        data.setBarcode(product.getBarcode());
        data.setProductName(product.getName());
        return data;
    }

    private double calculateOrderTotal(List<OrderItemPojo> orderItems) {
        return orderItems.stream().mapToDouble(item -> item.getQuantity() * item.getSellingPrice()).sum();
    }

    private OrderData buildOrderData(OrderPojo order, double total) {
        OrderData orderData = new OrderData();
        orderData.setId(order.getId());
        orderData.setTime(order.getCreatedAt());
        orderData.setTotal(total);
        return orderData;
    }

    private String generateInvoicePath(Integer orderId) {
        return "../invoices/order-" + orderId + ".pdf";
    }
}
