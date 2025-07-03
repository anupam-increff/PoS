package com.increff.pos.service;

import com.increff.invoice.InvoiceGenerator;
import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public byte[] downloadInvoice(String invoicePath) {
        if (Objects.isNull(invoicePath)) {
            throw new ApiException("No invoice was generated or associated for this order ");
        }
        try {
            byte[] pdf = Files.readAllBytes(Paths.get(invoicePath));
            return pdf;
        } catch (IOException e) {
            throw new ApiException("Invoice could not be read or regenerated: " + e.getMessage());
        }
    }

//    public void generateInvoice(Integer orderId) throws Exception {
//        OrderPojo order = orderService.get(orderId);
//        List<OrderItemPojo> orderItems = orderItemService.getByOrderId(orderId);
//
//        List<OrderItemData> itemDataList = new ArrayList<>();
//        double total = 0;
//
//        for (OrderItemPojo item : orderItems) {
//            ProductPojo product = productService.get(item.getProductId());
//            double lineTotal = item.getQuantity() * item.getSellingPrice();
//            total += lineTotal;
//
//            OrderItemData data = new OrderItemData();
//            data.setId(item.getId());
//            data.setOrderId(orderId);
//            data.setBarcode(product.getBarcode());
//            data.setProductName(product.getName());
//            data.setQuantity(item.getQuantity());
//            data.setSellingPrice(item.getSellingPrice());
//            itemDataList.addProduct(data);
//        }
//
//        OrderData invoice = new OrderData();
//        invoice.setId(order.getId());
//        invoice.setTime(order.getTime());
//        invoice.setTotal(total);
//        String path = "invoices/order-" + orderId + ".pdf";
//        invoice.setInvoicePath(path);
//
//        String base64Pdf = InvoiceGenerator.generate(invoice, itemDataList);
//        byte[] decodedPdf = Base64.getDecoder().decode(base64Pdf);
//
//        Files.createDirectories(Paths.get("invoices"));
//        Files.write(Paths.get(path), decodedPdf);
//
//        order.setInvoicePath(path);
//        order.setTotal(total);
//        orderService.update(orderId, order);
//    }
}
