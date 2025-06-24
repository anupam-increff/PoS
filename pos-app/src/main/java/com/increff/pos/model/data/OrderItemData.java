package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemData {
    private Integer id;
    private Integer orderId;
    private String barcode;
    private String productName;
    private Integer quantity;
    private Double sellingPrice;

    public OrderItemData() {
    }

    public OrderItemData(Integer id, String barcode, String productName, Integer quantity, Double sellingPrice) {
        this.id = id;
        this.barcode = barcode;
        this.productName = productName;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }
}
