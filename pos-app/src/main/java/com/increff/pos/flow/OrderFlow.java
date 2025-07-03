package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OrderFlow {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Transactional
    public Integer placeOrder(OrderForm orderForm) {
        List<OrderItemPojo> items = orderForm.getItems().stream().map(form -> {
            ProductPojo product = productService.getCheckProductByBarcode(form.getBarcode());

            OrderItemPojo pojo = new OrderItemPojo();
            pojo.setProductId(product.getId());
            pojo.setQuantity(form.getQuantity());
            pojo.setSellingPrice(form.getSellingPrice());
            return pojo;
        }).collect(Collectors.toList());

        for (OrderItemPojo item : items) {
            ProductPojo product = productService.getCheckProductById(item.getProductId());
            InventoryPojo inventory = inventoryService.getCheckByProductId(item.getProductId());
            if (inventory == null || inventory.getQuantity() < item.getQuantity()) {
                throw new ApiException("Insufficient inventory for: " + product.getName());
            }
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
//            inventoryService.update(inventory);
        }

        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        Integer orderId = orderService.createOrder(order);

        for (OrderItemPojo item : items) {
            item.setOrderId(orderId);
            orderItemService.add(item);
        }

        return orderId;
    }

    public OrderPojo get(Integer id) {
        return orderService.get(id);
    }
}
