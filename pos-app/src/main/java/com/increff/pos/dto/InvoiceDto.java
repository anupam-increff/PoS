package com.increff.pos.dto;

import com.increff.invoice.InvoiceGenerator;
import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.InvoiceFlow;
import com.increff.pos.pojo.InvoicePojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class InvoiceDto {

    @Autowired
    InvoiceFlow invoiceFlow;

    @Autowired
    ProductService productService;

    public ResponseEntity<byte[]> downloadInvoiceById(Integer invoiceId) {
        try {
            InvoicePojo invoice = invoiceFlow.getInvoiceById(invoiceId);
            byte[] invoiceBytes = Files.readAllBytes(Paths.get(invoice.getFilePath()));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + invoiceId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(invoiceBytes);
        } catch (IOException e) {
            throw new ApiException("Invoice could not be read: " + e.getMessage());
        }
    }

    public Integer generateInvoice(Integer orderId) {

        InvoicePojo invoice = invoiceFlow.generateInvoice(orderId);

        OrderPojo order = invoiceFlow.getOrderForInvoice(orderId);
        List<OrderItemPojo> orderItems = invoiceFlow.getOrderItemsForInvoice(orderId);

        OrderData orderData = convertOrderPojoToData(order);
        List<OrderItemData> orderItemDataList = convertOrderItemPojosToData(orderItems);

        generateAndSavePdfToFile(invoice, orderData, orderItemDataList);

        return invoice.getId();
    }

    private OrderData convertOrderPojoToData(OrderPojo order) {
        OrderData orderData = ConvertUtil.convert(order, OrderData.class);
        orderData.setTime(order.getCreatedAt());
        return orderData;
    }

    private List<OrderItemData> convertOrderItemPojosToData(List<OrderItemPojo> orderItems) {
        List<OrderItemData> itemDataList = new ArrayList<>();
        for (OrderItemPojo item : orderItems) {
            ProductPojo product = productService.getCheckProductById(item.getProductId());
            OrderItemData data = ConvertUtil.convert(item, OrderItemData.class);
            data.setBarcode(product.getBarcode());
            data.setProductName(product.getName());
            itemDataList.add(data);
        }
        return itemDataList;
    }

    private void generateAndSavePdfToFile(InvoicePojo invoice, OrderData orderData, List<OrderItemData> itemDataList) {
        try {
            String base64Pdf = InvoiceGenerator.generate(orderData, itemDataList);
            byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);

            Files.createDirectories(Paths.get("../invoices"));
            Files.write(Paths.get(invoice.getFilePath()), pdfBytes);

        } catch (IllegalArgumentException | TransformerException | org.apache.fop.apps.FOPException e) {
            throw new ApiException("Failed during PDF generation: " + e.getMessage());
        } catch (IOException e) {
            throw new ApiException("Failed to save invoice file: " + e.getMessage());
        }
    }
}
