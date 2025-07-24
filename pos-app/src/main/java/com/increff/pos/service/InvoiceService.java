package com.increff.pos.service;


import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InvoicePojo;
import com.increff.pos.dao.InvoiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;

@Service
@Transactional(rollbackFor = ApiException.class)
public class InvoiceService {

    @Autowired
    private InvoiceDao invoiceDao;

    public boolean checkIfInvoiceExistsForOrder(Integer orderId) {
        InvoicePojo invoice = invoiceDao.getByOrderId(orderId);
        return Objects.nonNull(invoice);
    }

    public Integer getInvoiceIdByOrderId(Integer orderId) {
        if (checkIfInvoiceExistsForOrder(orderId)) {
            InvoicePojo invoice = invoiceDao.getByOrderId(orderId);
            return invoice.getId();
        }
        return null;
    }

    public InvoicePojo createInvoiceRecord(OrderPojo order, List<OrderItemPojo> orderItems) {
        if (checkIfInvoiceExistsForOrder(order.getId())) {
            throw new ApiException("Invoice already exists for order ID: " + order.getId());
        }

        String invoiceFilePath = buildInvoiceFilePath(order.getId());
        InvoicePojo invoice = new InvoicePojo();
        invoice.setOrderId(order.getId());
        invoice.setFilePath(invoiceFilePath);
        invoiceDao.insert(invoice);
        return invoice;
    }

    public InvoicePojo getInvoiceById(Integer invoiceId) {
        InvoicePojo invoice = invoiceDao.getById(invoiceId);
        if (invoice == null) {
            throw new ApiException("Invoice not found for invoice id: " + invoiceId);
        }
        return invoice;
    }

    private String buildInvoiceFilePath(Integer orderId) {
        return "../invoices/order-" + orderId + ".pdf";
    }
}
