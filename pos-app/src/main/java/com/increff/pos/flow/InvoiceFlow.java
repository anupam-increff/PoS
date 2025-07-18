package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = ApiException.class)
public class InvoiceFlow {
    @Autowired
    private InvoiceService invoiceService;

    public byte[] getInvoice(Integer orderId) {
        return invoiceService.downloadInvoice(orderId);
    }

    public void generateInvoice(Integer orderId){
        invoiceService.generateInvoice(orderId);
    }
}
