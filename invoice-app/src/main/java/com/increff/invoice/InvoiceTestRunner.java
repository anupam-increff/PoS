package com.increff.invoice;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;

import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceTestRunner {

    public static void main(String[] args) throws Exception {
        // Sample OrderData
        OrderData order = new OrderData();
        order.setId(1001);
        order.setTime(ZonedDateTime.now());
        order.setInvoicePath("invoices/order-1001.pdf");

        // Sample OrderItemData list
        List<OrderItemData> items = new ArrayList<>();

        OrderItemData item1 = new OrderItemData();
        item1.setId(1);
        item1.setOrderId(order.getId());
        item1.setBarcode("ABC123");
        item1.setProductName("Test Product A");
        item1.setQuantity(2);
        item1.setSellingPrice(150.0);
        items.add(item1);

        OrderItemData item2 = new OrderItemData();
        item2.setId(2);
        item2.setOrderId(order.getId());
        item2.setBarcode("XYZ456");
        item2.setProductName("Test Product B");
        item2.setQuantity(1);
        item2.setSellingPrice(250.0);
        items.add(item2);

        // Generate invoice PDF
        String base64Pdf = InvoiceGenerator.generate(order, items);

        // Decode and save as file
        byte[] pdfBytes = java.util.Base64.getDecoder().decode(base64Pdf);
        try (FileOutputStream fos = new FileOutputStream("invoices/order-1001.pdf")) {
            fos.write(pdfBytes);
        }

        System.out.println("Invoice generated and saved as invoices/order-1001.pdf");
    }
}
