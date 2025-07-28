package com.increff.pos.order.integration.dto;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OrderCreationIntegrationTest extends AbstractTest {

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientService clientService;

    @Autowired
    private InventoryService inventoryService;

    private String testBarcode;

    @Before
    public void setUp() {
        createTestClients();
        createTestProduct();
        addInventory();
    }

    @Test
    public void testPlaceOrder() {
        OrderForm form = new OrderForm();
        form.setItems(Arrays.asList(createOrderItem(testBarcode, 2, 90.0)));

        OrderData order = orderDto.placeOrder(form);
        assertNotNull(order);
        assertEquals(OrderStatus.CREATED, order.getOrderStatus());

        List<OrderItemData> items = orderDto.getItemsByOrderId(order.getId());
        assertEquals(1, items.size());

        OrderItemData item = items.get(0);
        assertEquals(testBarcode, item.getBarcode());
        assertEquals(Integer.valueOf(2), item.getQuantity());
        assertEquals(Double.valueOf(90.0), item.getSellingPrice());
    }

    @Test
    public void testPlaceOrderMultipleItems() {
        String barcode2 = "TEST002";
        createProduct(barcode2, "Test Product 2", "TestClient1", 200.0);
        addInventoryForProduct(barcode2, 10);

        OrderForm form = new OrderForm();
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createOrderItem(testBarcode, 2, 90.0));
        items.add(createOrderItem(barcode2, 3, 180.0));
        form.setItems(items);

        OrderData order = orderDto.placeOrder(form);
        assertNotNull(order);
        assertEquals(OrderStatus.CREATED, order.getOrderStatus());

        List<OrderItemData> orderItems = orderDto.getItemsByOrderId(order.getId());
        assertEquals(2, orderItems.size());
    }

    @Test(expected = ApiException.class)
    public void testPlaceOrderInsufficientInventory() {
        OrderForm form = new OrderForm();
        form.setItems(Arrays.asList(createOrderItem(testBarcode, 20, 90.0)));

        orderDto.placeOrder(form);
    }

    @Test(expected = ApiException.class)
    public void testPlaceOrderNonExistentProduct() {
        OrderForm form = new OrderForm();
        form.setItems(Arrays.asList(createOrderItem("NONEXISTENT", 2, 90.0)));

        orderDto.placeOrder(form);
    }

    @Test(expected = ApiException.class)
    public void testPlaceOrderInvalidSellingPrice() {
        OrderForm form = new OrderForm();
        form.setItems(Arrays.asList(createOrderItem(testBarcode, 2, 110.0)));

        orderDto.placeOrder(form);
    }

    private void createTestClients() {
        createClient("TestClient1");
    }

    private void createClient(String name) {
        ClientPojo client = new ClientPojo();
        client.setName(name);
        clientService.addClient(client);
    }

    private void createTestProduct() {
        testBarcode = "TEST001";
        createProduct(testBarcode, "Test Product", "TestClient1", 100.0);
    }

    private void createProduct(String barcode, String name, String clientName, Double mrp) {
        ProductForm form = new ProductForm();
        form.setBarcode(barcode);
        form.setName(name);
        form.setClientName(clientName);
        form.setMrp(mrp);
        productDto.addProduct(form);
    }

    private void addInventory() {
        addInventoryForProduct(testBarcode, 10);
    }

    private void addInventoryForProduct(String barcode, Integer quantity) {
        ProductData product = productDto.getByBarcode(barcode);
        inventoryService.addInventory(product.getId(), quantity);
    }

    private OrderItemForm createOrderItem(String barcode, Integer quantity, Double sellingPrice) {
        OrderItemForm item = new OrderItemForm();
        item.setBarcode(barcode);
        item.setQuantity(quantity);
        item.setSellingPrice(sellingPrice);
        return item;
    }
} 