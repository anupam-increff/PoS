package com.increff.pos.service;

import com.increff.invoice.InvoiceGenerator;
import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.enums.InvoiceStatus;
import com.increff.pos.pojo.*;
import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.util.ConvertUtil;
import org.apache.fop.apps.FOPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = ApiException.class)
public class InvoiceService {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InvoiceDao invoiceDao;

    public byte[] downloadInvoice(Integer orderId) {
        InvoicePojo invoice = invoiceDao.getByOrderId(orderId);
        if (invoice == null) {
            throw new ApiException("Invoice not generated for order id: " + orderId);
        }
        try {
            return Files.readAllBytes(Paths.get(invoice.getFilePath()));
        } catch (IOException e) {
            throw new ApiException("Invoice could not be read: " + e.getMessage());
        }
    }

    public void generateInvoice(Integer orderId) {
        OrderPojo order = orderService.getCheckByOrderId(orderId);

        if ((order.getInvoiceGenerated())) {
            throw new ApiException("Invoice already exists for order ID: " + orderId);
        }

        List<OrderItemPojo> orderItems = orderItemService.getByOrderId(orderId);
        List<OrderItemData> itemDataList = buildOrderItemDataList(orderItems);
        double calculatedTotal = calculateOrderTotal(orderItems);

        OrderData orderData = buildOrderData(order, calculatedTotal);
        String invoicePath = generateInvoicePath(orderId);

        byte[] pdfBytes = generatePdfDocument(orderData, itemDataList);
        saveInvoiceToFile(invoicePath, pdfBytes);

        // Persist invoice record
        InvoicePojo invoice = new InvoicePojo();
        invoice.setOrderId(orderId);
        invoice.setFilePath(invoicePath);
        invoice.setStatus(InvoiceStatus.GENERATED);
        invoiceDao.insert(invoice);

        // update order status
        order.setInvoiceGenerated(true);
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

    private byte[] generatePdfDocument(OrderData orderData, List<OrderItemData> itemDataList) {
        try {
            String base64Pdf = InvoiceGenerator.generate(orderData, itemDataList);
            return Base64.getDecoder().decode(base64Pdf);
        } catch (IllegalArgumentException | TransformerException | FOPException e) {
            throw new ApiException("Failed during PDF generation: " + e.getMessage());
        }
    }

    private void saveInvoiceToFile(String invoicePath, byte[] pdfBytes) {
        try {
            Files.createDirectories(Paths.get("./invoices"));
            Files.write(Paths.get(invoicePath), pdfBytes);
        } catch (IOException e) {
            throw new ApiException("Failed to save invoice file: " + e.getMessage());
        }
    }

}
