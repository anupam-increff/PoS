package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
        List<OrderItemPojo> orderItemPojos = orderFormToListOfOrderItemPojo(orderForm);
        double total = 0;
        for (OrderItemPojo item : orderItemPojos) {
            ProductPojo product = productService.getCheckProductById(item.getProductId());
            InventoryPojo inventory = inventoryService.getCheckByProductId(item.getProductId());
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new ApiException("Insufficient inventory for: " + product.getName());
            }
            total += item.getSellingPrice() * item.getQuantity();
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
        }

        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order.setTotal(total);
        Integer orderId = orderService.createOrder(order);

        for (OrderItemPojo item : orderItemPojos) {
            item.setOrderId(orderId);
            orderItemService.add(item);
        }

        return orderId;
    }

    public OrderPojo getByOrderId(Integer id) {
        return orderService.getCheckByOrderId(id);
    }

    public List<OrderPojo> getAllOrders() {
        return orderService.getAllOrders();
    }

    public List<OrderItemData> getOrderItemsByOrderId(Integer orderId) {
        List<OrderItemPojo> orderItemPojos = orderItemService.getByOrderId(orderId);
        return orderItemPojos.stream().map(orderItemPojo -> {
            ProductPojo productPojo = productService.getCheckProductById(orderItemPojo.getProductId());
            OrderItemData orderItemData = ConvertUtil.convert(orderItemPojo, OrderItemData.class);
            orderItemData.setBarcode(productPojo.getBarcode());
            orderItemData.setProductName(productPojo.getName());
            return orderItemData;
        }).collect(Collectors.toList());

    }

    private List<OrderItemPojo> orderFormToListOfOrderItemPojo(OrderForm orderForm) {
        return orderForm.getItems().stream().map(form -> {
            ProductPojo product = productService.getCheckProductByBarcode(form.getBarcode());
            OrderItemPojo pojo = new OrderItemPojo();
            pojo.setProductId(product.getId());
            pojo.setQuantity(form.getQuantity());
            pojo.setSellingPrice(form.getSellingPrice());
            return pojo;
        }).collect(Collectors.toList());
    }
}
