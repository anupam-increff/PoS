package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceData {
    private Integer id;
    private Integer orderId;
    private String filePath;
} 