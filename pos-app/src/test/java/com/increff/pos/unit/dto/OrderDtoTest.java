package com.increff.pos.unit.dto;

import com.increff.pos.config.TestData;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderDtoTest {

    @Mock
    private OrderFlow orderFlow;

    @Mock
    private ProductService productService;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private OrderDto orderDto;

    private OrderPojo testOrder;
    private OrderForm testOrderForm;
    private List<OrderItemPojo> testOrderItems;

    @Before
    public void setUp() {
        testOrder = TestData.orderPojo();
        testOrder.setId(1);
        testOrder.setOrderStatus(OrderStatus.CREATED);
        
        OrderItemForm item1 = TestData.orderItemForm("BARCODE-001", 2, 99.99);
        OrderItemForm item2 = TestData.orderItemForm("BARCODE-002", 1, 149.99);
        testOrderForm = TestData.orderForm(Arrays.asList(item1, item2));
        
        testOrderItems = Arrays.asList(
            TestData.orderItemPojo(1, 1, 2, 99.99),
            TestData.orderItemPojo(2, 2, 1, 149.99)
        );
    }

    @Test
    public void testPlaceOrder_Success() {
        // Given
        when(productService.getCheckProductByBarcode("BARCODE-001"))
            .thenReturn(TestData.product(1, 1));
        when(productService.getCheckProductByBarcode("BARCODE-002"))
            .thenReturn(TestData.product(2, 1));
        when(orderFlow.placeOrder(any())).thenReturn(testOrder);
        when(invoiceService.getInvoiceIdByOrderId(1)).thenReturn(null);

        // When
        OrderData result = orderDto.placeOrder(testOrderForm);

        // Then
        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getId());
        assertEquals(OrderStatus.CREATED, result.getOrderStatus());
        assertNull(result.getInvoiceId());
        verify(orderFlow, times(1)).placeOrder(any());
    }

    @Test
    public void testPlaceOrder_WithInvoice() {
        // Given
        when(productService.getCheckProductByBarcode("BARCODE-001"))
            .thenReturn(TestData.product(1, 1));
        when(productService.getCheckProductByBarcode("BARCODE-002"))
            .thenReturn(TestData.product(2, 1));
        when(orderFlow.placeOrder(any())).thenReturn(testOrder);
        when(invoiceService.getInvoiceIdByOrderId(1)).thenReturn(5);

        // When
        OrderData result = orderDto.placeOrder(testOrderForm);

        // Then
        assertNotNull(result);
        assertEquals(Integer.valueOf(5), result.getInvoiceId());
        verify(invoiceService, times(1)).getInvoiceIdByOrderId(1);
    }

    @Test
    public void testGetAll_Success() {
        // Given
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderFlow.getAllOrders(0, 10)).thenReturn(orders);
        when(orderFlow.countAllOrders()).thenReturn(1L);
        when(invoiceService.getInvoiceIdByOrderId(1)).thenReturn(null);

        // When
        PaginatedResponse<OrderData> result = orderDto.getAll(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1L, result.getTotalItems());
        assertEquals(10, result.getPageSize());
        
        OrderData orderData = result.getContent().get(0);
        assertEquals(Integer.valueOf(1), orderData.getId());
        assertEquals(OrderStatus.CREATED, orderData.getOrderStatus());
    }

    @Test
    public void testGetAll_EmptyResult() {
        // Given
        when(orderFlow.getAllOrders(0, 10)).thenReturn(Arrays.asList());
        when(orderFlow.countAllOrders()).thenReturn(0L);

        // When
        PaginatedResponse<OrderData> result = orderDto.getAll(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalItems());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    public void testSearchOrders_Success() {
        // Given
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        List<OrderPojo> orders = Arrays.asList(testOrder);
        
        when(orderFlow.searchOrders(start, end, "test", 0, 10)).thenReturn(orders);
        when(orderFlow.countMatchingOrders(start, end, "test")).thenReturn(1L);
        when(invoiceService.getInvoiceIdByOrderId(1)).thenReturn(null);

        // When
        PaginatedResponse<OrderData> result = orderDto.searchOrders(start, end, "test", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        
        verify(orderFlow, times(1)).searchOrders(start, end, "test", 0, 10);
        verify(orderFlow, times(1)).countMatchingOrders(start, end, "test");
    }

    @Test
    public void testConvertFormToPojos_Success() {
        // Given
        when(productService.getCheckProductByBarcode("BARCODE-001"))
            .thenReturn(TestData.product(1, 1));
        when(productService.getCheckProductByBarcode("BARCODE-002"))
            .thenReturn(TestData.product(2, 1));
        when(orderFlow.placeOrder(any())).thenReturn(testOrder);
        when(invoiceService.getInvoiceIdByOrderId(1)).thenReturn(null);

        // When
        OrderData result = orderDto.placeOrder(testOrderForm);

        // Then
        verify(productService, times(1)).getCheckProductByBarcode("BARCODE-001");
        verify(productService, times(1)).getCheckProductByBarcode("BARCODE-002");
        assertNotNull(result);
    }

    @Test
    public void testConvertPojoToOrderData_WithInvoice() {
        // Given
        testOrder.setOrderStatus(OrderStatus.INVOICE_GENERATED);
        when(orderFlow.getAllOrders(0, 10)).thenReturn(Arrays.asList(testOrder));
        when(orderFlow.countAllOrders()).thenReturn(1L);
        when(invoiceService.getInvoiceIdByOrderId(1)).thenReturn(5);

        // When
        PaginatedResponse<OrderData> result = orderDto.getAll(0, 10);

        // Then
        OrderData orderData = result.getContent().get(0);
        assertEquals(OrderStatus.INVOICE_GENERATED, orderData.getOrderStatus());
        assertEquals(Integer.valueOf(5), orderData.getInvoiceId());
    }

    @Test
    public void testPaginationCalculation_MultiplePages() {
        // Given
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderFlow.getAllOrders(1, 5)).thenReturn(orders);
        when(orderFlow.countAllOrders()).thenReturn(12L);
        when(invoiceService.getInvoiceIdByOrderId(1)).thenReturn(null);

        // When
        PaginatedResponse<OrderData> result = orderDto.getAll(1, 5);

        // Then
        assertEquals(1, result.getCurrentPage());
        assertEquals(3, result.getTotalPages()); // ceil(12/5) = 3
        assertEquals(12L, result.getTotalItems());
        assertEquals(5, result.getPageSize());
    }

    @Test
    public void testSearchOrders_NoResults() {
        // Given
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        
        when(orderFlow.searchOrders(start, end, "nonexistent", 0, 10)).thenReturn(Arrays.asList());
        when(orderFlow.countMatchingOrders(start, end, "nonexistent")).thenReturn(0L);

        // When
        PaginatedResponse<OrderData> result = orderDto.searchOrders(start, end, "nonexistent", 0, 10);

        // Then
        assertEquals(0, result.getContent().size());
        assertEquals(0L, result.getTotalItems());
        assertEquals(0, result.getTotalPages());
    }
} 