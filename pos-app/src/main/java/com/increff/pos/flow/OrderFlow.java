package com.increff.pos.flow;

import com.increff.invoice.InvoiceGenerator;
import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import org.apache.fop.apps.FOPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
    public Integer placeOrder(OrderForm form) {
        List<OrderItemPojo> orderItems = new ArrayList<>();

        for (OrderItemForm itemForm : form.getItems()) {
            ProductPojo product = productService.getByBarcode(itemForm.getBarcode());
            if (product == null) {
                throw new ApiException("Invalid barcode: " + itemForm.getBarcode());
            }

            InventoryPojo inventory = inventoryService.getByBarcode(itemForm.getBarcode());
            if (inventory == null || inventory.getQuantity() < itemForm.getQuantity()) {
                throw new ApiException("Insufficient inventory for: " + product.getName());
            }

            inventory.setQuantity(inventory.getQuantity() - itemForm.getQuantity());

            OrderItemPojo orderItem = new OrderItemPojo();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemForm.getQuantity());
            orderItem.setSellingPrice(itemForm.getSellingPrice());
            orderItems.add(orderItem);
        }

        Integer orderId = orderService.createOrder(orderItems);

        try {
            generateInvoice(orderId);
        } catch (Exception e) {
            throw new ApiException("Invoice generation failed: " + e.getMessage());
        }

        return orderId;
    }

    public void generateInvoice(Integer orderId) throws Exception {
        OrderPojo order = orderService.get(orderId);
        List<OrderItemPojo> orderItems = orderItemService.getByOrderId(orderId);

        List<OrderItemData> itemDataList = new ArrayList<>();
        double total = 0;

        for (OrderItemPojo item : orderItems) {
            ProductPojo product = productService.get(item.getProductId());
            double lineTotal = item.getQuantity() * item.getSellingPrice();
            total += lineTotal;

            OrderItemData data = new OrderItemData();
            data.setId(item.getId());
            data.setOrderId(orderId);
            data.setBarcode(product.getBarcode());
            data.setProductName(product.getName());
            data.setQuantity(item.getQuantity());
            data.setSellingPrice(item.getSellingPrice());
            itemDataList.add(data);
        }

        OrderData invoice = new OrderData();
        invoice.setId(order.getId());
        invoice.setTime(order.getTime());
        invoice.setTotal(total);
        String path = "invoices/order-" + orderId + ".pdf";
        invoice.setInvoicePath(path);

        String base64Pdf = InvoiceGenerator.generate(invoice, itemDataList);
        byte[] decodedPdf = Base64.getDecoder().decode(base64Pdf);

        Files.createDirectories(Paths.get("invoices"));
        Files.write(Paths.get(path), decodedPdf);

        order.setInvoicePath(path);
        order.setTotal(total);
        orderService.update(orderId, order);
    }

    public OrderPojo get(Integer id) {
        return orderService.get(id);
    }
}
