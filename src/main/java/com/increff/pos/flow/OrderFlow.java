package com.increff.pos.flow;

import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderFlow {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    @Transactional
    public Integer placeOrder(OrderForm form) {
        List<OrderItemPojo> items = new ArrayList<>();

        for (OrderItemForm f : form.getItems()) {
            ProductPojo product = productService.getByBarcode(f.getBarcode());
            if (product == null) throw new ApiException("Invalid barcode: " + f.getBarcode());

            InventoryPojo inv = inventoryService.getByProductId(product.getId());
            if (inv == null || inv.getQuantity() < f.getQuantity()) {
                throw new ApiException("Insufficient inventory for: " + product.getName());
            }

            inv.setQuantity(inv.getQuantity() - f.getQuantity());

            OrderItemPojo p = new OrderItemPojo();
            p.setProductId(product.getId());
            p.setQuantity(f.getQuantity());
            p.setSellingPrice(f.getSellingPrice());

            items.add(p);
        }

        return orderService.createOrder(items);
    }
}
