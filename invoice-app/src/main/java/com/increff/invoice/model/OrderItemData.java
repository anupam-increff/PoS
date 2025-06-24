package com.increff.invoice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class OrderItemData {
    private Integer id;
    private Integer orderId;
    private String barcode;
    private String productName;
    private Integer quantity;
    private Double sellingPrice;

    // keep if you need, but must be public:
    public OrderItemData(Integer id, Integer orderId,
                         String barcode, String productName,
                         Integer quantity, Double sellingPrice) {
        this.id = id;
        this.orderId = orderId;
        this.barcode = barcode;
        this.productName = productName;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }
}
