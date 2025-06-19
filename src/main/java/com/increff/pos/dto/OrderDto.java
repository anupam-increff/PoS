package com.increff.pos.dto;

import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    public Integer placeOrder(@Valid OrderForm form) {
        List<OrderItemPojo> items = form.getItems().stream().map(f -> {
            ProductPojo product = productService.getByBarcode(f.getBarcode());
            if (product == null) throw new RuntimeException("Invalid barcode " + f.getBarcode());

            OrderItemPojo p = new OrderItemPojo();
            p.setProductId(product.getId());
            p.setQuantity(f.getQuantity());
            p.setSellingPrice(f.getSellingPrice());
            return p;
        }).collect(Collectors.toList());

        return orderService.createOrder(items);
    }
}