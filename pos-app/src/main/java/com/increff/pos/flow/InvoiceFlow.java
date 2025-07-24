package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InvoicePojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = ApiException.class)
public class InvoiceFlow {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private OrderService orderService;

    public InvoicePojo generateInvoice(Integer orderId) {
        OrderPojo order = orderService.getCheckByOrderId(orderId);
        List<OrderItemPojo> orderItems = orderService.getOrderItemsByOrderId(orderId);
        return invoiceService.createInvoiceRecord(order, orderItems);
    }

    public InvoicePojo getInvoiceById(Integer invoiceId) {
        return invoiceService.getInvoiceById(invoiceId);
    }

    public OrderPojo getOrderForInvoice(Integer orderId) {
        return orderService.getCheckByOrderId(orderId);
    }

    public List<OrderItemPojo> getOrderItemsForInvoice(Integer orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
    }
}
