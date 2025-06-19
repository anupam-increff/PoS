package com.increff.pos.model.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class OrderItemData {
    private Integer id;
    private Integer orderId;
    private String barcode;
    private String productName;
    private Integer quantity;
    private Double sellingPrice;
}

