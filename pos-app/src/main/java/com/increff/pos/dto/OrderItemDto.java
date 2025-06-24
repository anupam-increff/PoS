package com.increff.pos.dto;

import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderItemDto {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    public List<OrderItemData> getByOrderId(Integer orderId) {
        List<OrderItemData> out = new ArrayList<>();
        orderItemService.getByOrderId(orderId).forEach(item -> {
            ProductPojo p = productService.get(item.getProductId());

            OrderItemData d = new OrderItemData();
            d.setId(item.getId());
            d.setOrderId(item.getOrderId());
            d.setBarcode(p.getBarcode());
            d.setProductName(p.getName());
            d.setQuantity(item.getQuantity());
            d.setSellingPrice(item.getSellingPrice());

            out.add(d);
        });
        return out;
    }
}
