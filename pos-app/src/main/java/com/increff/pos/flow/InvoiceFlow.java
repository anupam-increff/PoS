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
import org.apache.fop.apps.FOPException;
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
    public void generateInvoice(Integer orderId) throws Exception {
        OrderPojo orderPojo = getOrderById(orderId);
        if (!Objects.isNull(orderPojo.getInvoicePath())) {
            throw new ApiException("Invoice was already generated for order with Id : " + orderId + " try downloading!");
        }
        String path = "invoices/order-" + orderId + ".pdf";
        orderPojo.setInvoicePath(path);
        OrderData orderData = ConvertUtil.convert(orderPojo, OrderData.class);
        List<OrderItemPojo> orderItemPojos = orderService.getOrderItems(orderId);
        List<OrderItemData> orderItemDataList = orderItemPojos.stream().map(orderItemPojo -> ConvertUtil.convert(orderItemPojo, OrderItemData.class)).collect(Collectors.toList());
        String base64Pdf = InvoiceGenerator.generate(orderData, orderItemDataList);
        byte[] decodedPdf = Base64.getDecoder().decode(base64Pdf);

        try {
            Files.createDirectories(Paths.get("././invoices"));
            Files.write(Paths.get(path), decodedPdf);
        } catch (Exception e) {
            throw new ApiException( e.getMessage());
        }

    }
}
