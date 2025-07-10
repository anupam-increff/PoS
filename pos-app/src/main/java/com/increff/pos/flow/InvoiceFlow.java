package com.increff.pos.flow;

import com.increff.invoice.InvoiceGenerator;
import com.increff.pos.exception.ApiException;
import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class InvoiceFlow {
    @Autowired
    private OrderService orderService;
    @Autowired
    private InvoiceService invoiceService;

    public OrderPojo getOrderById(Integer orderId) {
        return orderService.getCheckByOrderId(orderId);
    }

    public byte[] getInvoice(Integer orderId) {
        OrderPojo orderPojo = getOrderById(orderId);
        return invoiceService.downloadInvoice(orderPojo.getInvoicePath());
    }

    @Transactional
    public void generateInvoice(Integer orderId){
        OrderPojo orderPojo = getOrderById(orderId);
        validateInvoiceNotExists(orderPojo, orderId);
        
        String path = buildInvoicePath(orderId);
        orderPojo.setInvoicePath(path);
        
        byte[] pdfBytes = generatePdfBytes(orderId);
        saveInvoiceFile(path, pdfBytes);
    }

    private void validateInvoiceNotExists(OrderPojo orderPojo, Integer orderId) {
        if (!Objects.isNull(orderPojo.getInvoicePath())) {
            throw new ApiException("Invoice was already generated for order with Id : " + orderId + " try downloading!");
        }
    }

    private String buildInvoicePath(Integer orderId) {
        return "invoices/order-" + orderId + ".pdf";
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
                .map(orderItemPojo -> ConvertUtil.convert(orderItemPojo, OrderItemData.class))
                .collect(Collectors.toList());
    }

    private void saveInvoiceFile(String path, byte[] pdfBytes) {
        try {
            Files.createDirectories(Paths.get("././invoices"));
            Files.write(Paths.get(path), pdfBytes);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
