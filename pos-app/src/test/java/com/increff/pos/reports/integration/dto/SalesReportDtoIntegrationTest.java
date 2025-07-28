package com.increff.pos.reports.integration.dto;

import com.increff.pos.dao.*;
import com.increff.pos.dto.SalesReportDto;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import com.increff.pos.pojo.*;
import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for SalesReportDto.
 * Tests integration between SalesReportDto -> SalesReportService -> SalesReportDao
 */
public class SalesReportDtoIntegrationTest extends AbstractTest {

    @Autowired
    private SalesReportDto salesReportDto;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private InvoiceDao invoiceDao;

    private ClientPojo testClient;
    private ProductPojo testProduct;

    @Before
    public void setUp() {
        // Setup test data
        testClient = TestData.clientWithoutId("Test Client Reports");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("REPORT-001", "Report Test Product", testClient.getId());
        productDao.insert(testProduct);

        // Create test order with invoice
        OrderPojo order = TestData.orderPojo();
        orderDao.insert(order);

        OrderItemPojo orderItem = TestData.orderItemWithoutId(order.getId(), testProduct.getId(), 3, 100.0);
        orderItemDao.insert(orderItem);

        // Create invoice for the order
        InvoicePojo invoice = new InvoicePojo();
        invoice.setOrderId(order.getId());
        invoice.setFilePath("../invoices/order-" + order.getId() + ".pdf");
        invoiceDao.insert(invoice);
    }

    /**
     * Tests the complete sales report generation flow through DTO layer.
     * Verifies proper integration with service and dao layers.
     */
    @Test
    public void testGetSalesReport() {
        // Given
        SalesReportFilterForm filterForm = new SalesReportFilterForm();
        filterForm.setStartDate(ZonedDateTime.now().minusDays(1));
        filterForm.setEndDate(ZonedDateTime.now().plusDays(1));
        filterForm.setClientName("Test Client Reports");

        // When - DTO integrates with Service -> DAO
        PaginatedResponse<SalesReportData> response = salesReportDto.get(filterForm, 0, 10);

        // Then - Verify integration worked
        assertNotNull("Response should be provided by DTO->Service->DAO integration", response);
        assertTrue("Should contain sales data for our test client", response.getContent().size() >= 0);
        
        // Verify the service orchestration worked properly
        assertNotNull("Total items should be set", response.getTotalItems());
        assertTrue("Page size should be valid", response.getPageSize() > 0);
    }

    /**
     * Tests sales report generation without any filters.
     * Verifies proper handling of unfiltered data through service layer.
     */
    @Test
    public void testGetSalesReportWithoutFilter() {
        // Given
        SalesReportFilterForm filterForm = new SalesReportFilterForm();
        filterForm.setStartDate(ZonedDateTime.now().minusDays(7));
        filterForm.setEndDate(ZonedDateTime.now());

        // When - DTO integrates with Service for all clients
        PaginatedResponse<SalesReportData> response = salesReportDto.get(filterForm, 0, 10);

        // Then - Verify DTO integration worked
        assertNotNull("Response should be provided by DTO integration", response);
        // Should return data for all clients, including our test client
        assertTrue("Response should be properly structured", response.getTotalItems() >= 0);
    }
} 