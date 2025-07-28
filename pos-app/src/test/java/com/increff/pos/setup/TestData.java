package com.increff.pos.setup;

import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;

import java.time.ZonedDateTime;
import java.util.List;

public final class TestData {
    
    private TestData() {}
    
    /* ---------- Client ---------- */
    public static ClientPojo client(int id) {
        ClientPojo client = new ClientPojo();
        client.setId(id);
        client.setName("Client-" + id);
        return client;
    }
    
    public static ClientPojo clientWithoutId(String name) {
        ClientPojo client = new ClientPojo();
        client.setName(name);
        return client;
    }
    
    /* ---------- Product ---------- */
    public static ProductPojo product(int id, int clientId) {
        ProductPojo product = new ProductPojo();
        product.setId(id);
        product.setBarcode("BARCODE-" + id);
        product.setName("Product-" + id);
        product.setClientId(clientId);
        product.setMrp(99.99);
        product.setImageUrl("image-" + id + ".jpg");
        return product;
    }
    
    public static ProductPojo productWithoutId(String barcode, String name, int clientId) {
        ProductPojo product = new ProductPojo();
        product.setBarcode(barcode);
        product.setName(name);
        product.setClientId(clientId);
        product.setMrp(99.99);
        product.setImageUrl("image.jpg");
        return product;
    }
    
    public static ProductForm productForm(String barcode, String name, String clientName, Double mrp) {
        ProductForm form = new ProductForm();
        form.setBarcode(barcode);
        form.setName(name);
        form.setClientName(clientName);
        form.setMrp(mrp);
        form.setImageUrl("image.jpg");
        return form;
    }
    
    /* ---------- Inventory ---------- */
    public static InventoryPojo inventory(int id, int productId) {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setId(id);
        inventory.setProductId(productId);
        inventory.setQuantity(100);
        return inventory;
    }
    
    public static InventoryPojo inventoryWithoutId(int productId, Integer quantity) {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);
        return inventory;
    }

    /* ---------- Order ---------- */
    public static OrderPojo order(int id) {
        OrderPojo order = new OrderPojo();
        order.setId(id);
        return order;
    }
    
    public static OrderPojo orderPojo() {
        OrderPojo order = new OrderPojo();
        return order;
    }
    
    public static OrderPojo orderWithoutId() {
        OrderPojo order = new OrderPojo();
        return order;
    }
    
    public static OrderForm orderForm(List<OrderItemForm> items) {
        OrderForm form = new OrderForm();
        form.setItems(items);
        return form;
    }
    
    public static OrderItemPojo orderItemPojo(int id, int productId, int quantity, double sellingPrice) {
        OrderItemPojo item = new OrderItemPojo();
        item.setId(id);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setSellingPrice(sellingPrice);
        return item;
    }
    
    public static OrderItemPojo orderItemWithoutId(int orderId, int productId, Integer quantity, Double sellingPrice) {
        OrderItemPojo item = new OrderItemPojo();
        item.setOrderId(orderId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setSellingPrice(sellingPrice);
        return item;
    }
    
    public static OrderItemForm orderItemForm(String barcode, Integer quantity, Double sellingPrice) {
        OrderItemForm form = new OrderItemForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);
        form.setSellingPrice(sellingPrice);
        return form;
    }
} 