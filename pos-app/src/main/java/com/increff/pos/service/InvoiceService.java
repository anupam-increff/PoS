package com.increff.pos.service;

import com.increff.invoice.InvoiceGenerator;
import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class InvoiceService {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    public byte[] downloadInvoice(String invoicePath) {
        validateInvoicePath(invoicePath);
        try {
            return Files.readAllBytes(Paths.get(invoicePath));
        } catch (IOException e) {
            throw new ApiException("Invoice could not be read: " + e.getMessage());
        }
    }

    @Transactional(rollbackFor = ApiException.class)
    public void generateInvoice(Integer orderId) {
        OrderPojo order = orderService.getCheckByOrderId(orderId);
        validateOrderForInvoiceGeneration(order, orderId);
        
        List<OrderItemPojo> orderItems = orderItemService.getByOrderId(orderId);
        List<OrderItemData> itemDataList = buildOrderItemDataList(orderItems);
        double calculatedTotal = calculateOrderTotal(orderItems);
        
        OrderData orderData = buildOrderData(order, calculatedTotal);
        String invoicePath = generateInvoicePath(orderId);
        
        byte[] pdfBytes = generatePdfDocument(orderData, itemDataList);
        saveInvoiceToFile(invoicePath, pdfBytes);
        
        updateOrderWithInvoice(order, invoicePath, calculatedTotal);
    }

    private void validateInvoicePath(String invoicePath) {
        if (Objects.isNull(invoicePath)) {
            throw new ApiException("No invoice was generated or associated for this order");
        }
    }

    private void validateOrderForInvoiceGeneration(OrderPojo order, Integer orderId) {
        if (!Objects.isNull(order.getInvoicePath())) {
            throw new ApiException("Invoice already exists for order ID: " + orderId);
        }
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

    private OrderItemData createOrderItemData(OrderItemPojo item, ProductPojo product) {
        OrderItemData data = new OrderItemData();
        data.setId(item.getId());
        data.setOrderId(item.getOrderId());
        data.setBarcode(product.getBarcode());
        data.setProductName(product.getName());
        data.setQuantity(item.getQuantity());
        data.setSellingPrice(item.getSellingPrice());
        return data;
    }

    private double calculateOrderTotal(List<OrderItemPojo> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getSellingPrice())
                .sum();
    }

    private OrderData buildOrderData(OrderPojo order, double total) {
        OrderData orderData = new OrderData();
        orderData.setId(order.getId());
        orderData.setTime(order.getTime());
        orderData.setTotal(total);
        return orderData;
    }

    private String generateInvoicePath(Integer orderId) {
        return "invoices/order-" + orderId + ".pdf";
    }

    private byte[] generatePdfDocument(OrderData orderData, List<OrderItemData> itemDataList) {
        try {
            String base64Pdf = InvoiceGenerator.generate(orderData, itemDataList);
            return Base64.getDecoder().decode(base64Pdf);
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid base64 format in PDF generation: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException("PDF generation failed: " + e.getMessage());
        }
    }

    private void saveInvoiceToFile(String invoicePath, byte[] pdfBytes) {
        try {
            Files.createDirectories(Paths.get("invoices"));
            Files.write(Paths.get(invoicePath), pdfBytes);
        } catch (IOException e) {
            throw new ApiException("Failed to save invoice file: " + e.getMessage());
        }
    }

    private void updateOrderWithInvoice(OrderPojo order, String invoicePath, double total) {
        order.setInvoicePath(invoicePath);
        order.setTotal(total);
        orderService.update(order.getId(), order);
    }
}
