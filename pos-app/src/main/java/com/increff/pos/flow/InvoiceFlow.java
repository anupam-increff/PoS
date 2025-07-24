package com.increff.pos.flow;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.exception.ApiException;
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

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(rollbackFor = ApiException.class)
public class InvoiceFlow {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;

    public byte[] getInvoiceById(Integer invoiceId) {
        return invoiceService.downloadInvoiceById(invoiceId);
    }

    public Integer generateInvoice(Integer orderId) {
        if (invoiceService.getInvoiceStatus(orderId)) {
            throw new ApiException("Invoice already exists for order ID: " + orderId);
        }

        OrderPojo order = orderService.getCheckByOrderId(orderId);
        List<OrderItemPojo> orderItems = orderService.getOrderItemsByOrderId(orderId);

        List<OrderItemData> itemDataList = buildInvoiceItemDataList(orderItems);
        OrderData orderData = buildInvoiceOrderData(order);
        String invoicePath = generateInvoiceFilePath(orderId);

        invoiceService.createInvoice(orderId, invoicePath, orderData, itemDataList);
        return invoiceService.getInvoiceIdByOrderId(orderId);
    }

    private List<OrderItemData> buildInvoiceItemDataList(List<OrderItemPojo> orderItems) {
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

    private OrderData buildInvoiceOrderData(OrderPojo order) {
        OrderData orderData = ConvertUtil.convert(order,OrderData.class);
        orderData.setTime(order.getCreatedAt());
        return orderData;
    }

    private String generateInvoiceFilePath(Integer orderId) {
        return "../invoices/order-" + orderId + ".pdf";
    }
}
