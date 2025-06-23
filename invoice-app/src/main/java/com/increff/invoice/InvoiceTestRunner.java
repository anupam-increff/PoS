package com.increff.invoice;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InvoiceTestRunner {
    public static void main(String[] args) throws Exception {
        // Sample Order
        OrderData order = OrderData.builder()
                .id(1)
                .time(ZonedDateTime.now())
                .invoicePath(null)
                .build();

        // Sample Order Items
        List<OrderItemData> items = Arrays.asList(
                OrderItemData.builder()
                        .orderId(1)
                        .barcode("abc123")
                        .productName("KitKat 100g")
                        .quantity(2)
                        .sellingPrice(49.99)
                        .build(),
                OrderItemData.builder()
                        .orderId(1)
                        .barcode("xyz789")
                        .productName("Dairy Milk 200g")
                        .quantity(1)
                        .sellingPrice(99.50)
                        .build()
        );

        // Generate Base64 PDF
        String base64 = InvoiceGenerator.generate(order, items);

        // Decode and save as PDF
        byte[] pdfBytes = Base64.getDecoder().decode(base64);
        Files.write(Paths.get("test-invoice.pdf"), pdfBytes);

        System.out.println("Invoice generated at test-invoice.pdf");
    }
}
