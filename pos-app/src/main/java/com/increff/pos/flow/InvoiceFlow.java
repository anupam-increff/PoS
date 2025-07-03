package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InvoiceFlow {
    @Autowired
    private OrderService orderService;
    @Autowired
    private InvoiceService invoiceService;

    public OrderPojo getOrderById(Integer orderId) {
        return orderService.get(orderId);
    }

    public byte[] getInvoice(Integer orderId) {
        OrderPojo orderPojo = getOrderById(orderId);
        return invoiceService.downloadInvoice(orderPojo.getInvoicePath());
    }
    public void generateInvoice(Integer orderId){
        OrderPojo orderPojo = getOrderById(orderId);
        if(!Objects.isNull(orderPojo.getInvoicePath())){
            throw new ApiException("Invoice was already generated for order with Id : " +orderId);
        }

    }
}
