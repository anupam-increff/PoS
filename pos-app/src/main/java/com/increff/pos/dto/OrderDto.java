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
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDto {

    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceService invoiceService;

    public OrderData placeOrder(@Valid OrderForm form) {
        List<OrderItemPojo> orderItemPojos = convertFormToPojos(form);
        return convertOrderPojoToData(orderFlow.placeOrder(orderItemPojos));
    }

    public PaginatedResponse<OrderData> getAll(int page, int size) {
        List<OrderPojo> orderPojos=orderFlow.getAllOrders(page, size);
        long total = orderFlow.countAllOrders();
        int totalPages = (int) Math.ceil((double) total / size);
        List<OrderData> data = orderPojos.stream().map(this::convertOrderPojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(data, page, totalPages, total, size);
    }

    public List<OrderItemData> getItemsByOrderId(Integer id) {
        List<OrderItemPojo> orderItems = orderFlow.getOrderItemsByOrderId(id);
        return orderItems.stream().map(this::convertToOrderItemPojoToData).collect(Collectors.toList());
    }

    public PaginatedResponse<OrderData> searchOrders(ZonedDateTime start, ZonedDateTime end, String query, int page, int size) {
        List<OrderPojo> pojos = orderFlow.searchOrders(start, end, query, page, size);
        long totalItems = orderFlow.countMatchingOrders(start, end, query);
        int totalPages = (int) Math.ceil((double) totalItems / size);

        List<OrderData> dataList = pojos.stream().map(this::convertOrderPojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, size);
    }

    public PaginatedResponse<OrderData> searchOrdersByForm(OrderSearchForm form) {
        return searchOrders(form.getStartDate(), form.getEndDate(), form.getQuery(), form.getPage(), form.getSize());
    }

    private List<OrderItemPojo> convertFormToPojos(OrderForm form) {
        return form.getItems().stream()
                .map(this::convertOrderItemFormToPojo)
                .collect(Collectors.toList());
    }

    private OrderItemPojo convertOrderItemFormToPojo(OrderItemForm itemForm) {
        ProductPojo product = productService.getCheckProductByBarcode(itemForm.getBarcode());
        OrderItemPojo orderItemPojo = ConvertUtil.convert(itemForm,OrderItemPojo.class);
        orderItemPojo.setProductId(product.getId());
        return orderItemPojo;
    }

    private OrderItemData convertToOrderItemPojoToData(OrderItemPojo item) {
        ProductPojo product = productService.getCheckProductById(item.getProductId());
        OrderItemData orderItemData = ConvertUtil.convert(item, OrderItemData.class);
        orderItemData.setBarcode(product.getBarcode());
        orderItemData.setProductName(product.getName());
        return orderItemData;
    }

    private OrderData convertOrderPojoToData(OrderPojo pojo) {
        OrderData orderData = ConvertUtil.convert(pojo, OrderData.class);
        orderData.setPlacedAt(pojo.getCreatedAt());
        orderData.setOrderStatus(pojo.getOrderStatus());
        Integer invoiceId = invoiceService.getInvoiceIdByOrderId(pojo.getId());
        orderData.setInvoiceId(invoiceId);
        return orderData;
    }
}