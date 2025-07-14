package com.increff.pos.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderItemData {
    private Integer id;
    private Integer orderId;
    private String barcode;
    private String productName;
    private Integer quantity;
    private Double sellingPrice;

}
