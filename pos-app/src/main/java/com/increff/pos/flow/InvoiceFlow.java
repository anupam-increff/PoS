package com.increff.pos.flow;

import com.increff.invoice.InvoiceGenerator;
import com.increff.pos.exception.ApiException;
import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Transactional(rollbackFor = ApiException.class)
public class InvoiceFlow {
    @Autowired
    private OrderService orderService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private ProductService productService;

    public byte[] getInvoice(Integer orderId) {
        OrderPojo orderPojo = getOrderById(orderId);
        return invoiceService.downloadInvoice(orderPojo.getInvoicePath());
    }

    public void generateInvoice(Integer orderId){
        OrderPojo orderPojo = getOrderById(orderId);
        validateInvoiceNotExists(orderPojo, orderId);
        
        String path = buildInvoicePath(orderId);
        orderPojo.setInvoicePath(path);
        
        byte[] pdfBytes = generatePdfBytes(orderId);
        saveInvoiceFile(path, pdfBytes);
    }

    private OrderPojo getOrderById(Integer orderId) {
        return orderService.getCheckByOrderId(orderId);
    }

    private void validateInvoiceNotExists(OrderPojo orderPojo, Integer orderId) {
        if (!Objects.isNull(orderPojo.getInvoicePath())) {
            throw new ApiException("Invoice was already generated for order with Id : " + orderId + " try downloading!");
        }
    }

    private String buildInvoicePath(Integer orderId) {
        return "./invoices/order-" + orderId + ".pdf";
    }

    private byte[] generatePdfBytes(Integer orderId){
        OrderData orderData = ConvertUtil.convert(getOrderById(orderId), OrderData.class);
        List<OrderItemData> orderItemDataList = getOrderItemDataList(orderId);
        try {
            String base64Pdf = InvoiceGenerator.generate(orderData, orderItemDataList);
            return Base64.getDecoder().decode(base64Pdf);
        }
        catch (Exception e){
            throw new ApiException("Failed to generate invoice :" +e.getMessage());
        }
    }

    private List<OrderItemData> getOrderItemDataList(Integer orderId) {
        List<OrderItemPojo> orderItemPojos = orderService.getOrderItems(orderId);
        return orderItemPojos.stream()
                .map(this::convertToOrderItemData)
                .collect(Collectors.toList());
    }

    private OrderItemData convertToOrderItemData(OrderItemPojo orderItemPojo) {
        OrderItemData data = ConvertUtil.convert(orderItemPojo, OrderItemData.class);
        ProductPojo product = productService.getCheckProductById(orderItemPojo.getProductId());
        data.setBarcode(product.getBarcode());
        data.setProductName(product.getName());
        return data;
    }

    private void saveInvoiceFile(String path, byte[] pdfBytes) {
        try {
            Files.createDirectories(Paths.get("././invoices"));
            Files.write(Paths.get(path), pdfBytes);
        } catch (IOException e) {
            throw new ApiException("IOException while saving invoice " + e.getMessage());
        }
    }
}
