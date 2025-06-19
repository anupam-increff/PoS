package com.increff.pos.dto;

import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemDto {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    public List<OrderItemData> getByOrderId(Integer orderId) {
        return orderItemService.getByOrderId(orderId).stream().map(item -> {
            ProductPojo p = productService.get(item.getProductId());
            return OrderItemData.builder()
                    .id(item.getId())
                    .orderId(item.getOrderId())
                    .barcode(p.getBarcode())
                    .productName(p.getName())
                    .quantity(item.getQuantity())
                    .sellingPrice(item.getSellingPrice())
                    .build();
        }).collect(Collectors.toList());
    }
}
