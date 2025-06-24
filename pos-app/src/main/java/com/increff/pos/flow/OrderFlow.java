package com.increff.pos.flow;

import com.increff.invoice.InvoiceGenerator;
import com.increff.invoice.model.OrderData ;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class OrderFlow {

    @Autowired private ProductService productService;
    @Autowired private InventoryService inventoryService;
    @Autowired private OrderService orderService;
    @Autowired private OrderItemService orderItemService;

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

        Integer orderId = orderService.createOrder(items);

        try {
            generateInvoice(orderId);
        } catch (Exception e) {
            throw new ApiException("Invoice generation failed: " + e.getMessage());
        }
        return orderId;
    }

    private void generateInvoice(Integer orderId) throws Exception {
        OrderPojo order = orderService.get(orderId);
        List<OrderItemPojo> pojos = orderItemService.getByOrderId(orderId);

        OrderData od = new OrderData();
        od.setId(order.getId());
        od.setTime(order.getTime());
        od.setInvoicePath("invoices/order-" + orderId + ".pdf");

        double total = 0;
        List<OrderItemData> items = new ArrayList<>();

        for (OrderItemPojo p : pojos) {
            ProductPojo pr = productService.get(p.getProductId());
            double line = p.getQuantity() * p.getSellingPrice();
            total += line;

            OrderItemData d = new OrderItemData();
            d.setId(p.getId());
            d.setOrderId(orderId);
            d.setBarcode(pr.getBarcode());
            d.setProductName(pr.getName());
            d.setQuantity(p.getQuantity());
            d.setSellingPrice(p.getSellingPrice());
            items.add(d);
        }

        od.setTotal(total);

        // Generate PDF
        String base64 = InvoiceGenerator.generate(od, items);
        byte[] pdf = Base64.getDecoder().decode(base64);

        Files.createDirectories(Paths.get("invoices")); // Ensure folder
        Files.write(Paths.get("invoices/order-" + orderId + ".pdf"), pdf);

        // ✅ Save path + total to DB
        OrderPojo updated = new OrderPojo();
        updated.setInvoicePath(od.getInvoicePath());
        updated.setTotal(total);
        orderService.update(orderId, updated);
    }



}
