package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderSearchForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OrderDto {

    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceService invoiceService;

    public OrderData placeOrder(@Valid OrderForm form) {
        List<OrderItemPojo> orderItemPojos = convertFormToPojos(form);
        OrderPojo orderPojo = orderFlow.placeOrder(orderItemPojos);
        return convertToData(orderPojo);
    }

    public PaginatedResponse<OrderData> getAll(Integer page, Integer pageSize) {
        List<OrderPojo> orders = orderService.getAllOrdersPaginated(page, pageSize);
        Long totalOrders = orderService.countAll();
        List<OrderData> orderDataList = orders.stream().map(this::convertToData).collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(orderDataList, page, pageSize, totalOrders);
    }

    public List<OrderItemData> getItemsByOrderId(Integer orderId) {
        List<OrderItemPojo> orderItems = orderService.getOrderItemsByOrderId(orderId);
        return orderItems.stream().map(this::convertToData).collect(Collectors.toList());
    }

    public PaginatedResponse<OrderData> searchOrders(ZonedDateTime startDate, ZonedDateTime endDate, String query, Integer page, Integer pageSize) {
        List<OrderPojo> orders = orderService.searchOrderByQuery(startDate, endDate, query, page, pageSize);
        Long totalOrders = orderService.countMatchingOrdersByQuery(startDate, endDate, query);
        List<OrderData> orderDataList = orders.stream().map(this::convertToData).collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(orderDataList, page, pageSize, totalOrders);
    }

    public PaginatedResponse<OrderData> searchOrdersByForm(OrderSearchForm form) {
        return searchOrders(form.getStartDate(), form.getEndDate(), form.getQuery(), 0, 10);
    }

    private List<OrderItemPojo> convertFormToPojos(OrderForm form) {
        return form.getItems().stream()
                .map(this::convertOrderItemFormToPojo)
                .collect(Collectors.toList());
    }

    private OrderItemPojo convertOrderItemFormToPojo(OrderItemForm itemForm) {
        ProductPojo product = productService.getCheckProductByBarcode(itemForm.getBarcode());
        OrderItemPojo orderItemPojo = ConvertUtil.convert(itemForm, OrderItemPojo.class);
        orderItemPojo.setProductId(product.getId());
        return orderItemPojo;
    }

    private OrderData convertToData(OrderPojo pojo) {
        OrderData orderData = ConvertUtil.convert(pojo, OrderData.class);
        orderData.setPlacedAt(pojo.getCreatedAt());
        orderData.setOrderStatus(pojo.getOrderStatus());

        // Get invoice ID if exists
        Integer invoiceId = invoiceService.getInvoiceIdByOrderId(pojo.getId());
        if (Objects.nonNull(invoiceId)) {
            orderData.setInvoiceId(invoiceId);
        }

        return orderData;
    }

    private OrderItemData convertToData(OrderItemPojo pojo) {
        ProductPojo product = productService.getCheckProductById(pojo.getProductId());
        OrderItemData orderItemData = ConvertUtil.convert(pojo, OrderItemData.class);
        orderItemData.setBarcode(product.getBarcode());
        orderItemData.setProductName(product.getName());
        return orderItemData;
    }
}