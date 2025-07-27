package com.increff.pos.reports.unit.service;

import com.increff.pos.dao.SalesReportDao;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.service.SalesReportService;
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
import com.increff.pos.model.form.SalesReportFilterForm;

@RunWith(MockitoJUnitRunner.class)
public class SalesReportServiceTest {

    @Mock
    private SalesReportDao salesReportDao;

    @InjectMocks
    private SalesReportService salesReportService;

    private SalesReportData testSalesReport;

    @Before
    public void setUp() {
        testSalesReport = new SalesReportData();
        testSalesReport.setClient("Test Client");
        testSalesReport.setQuantity(25L);
        testSalesReport.setRevenue(1500.0);
    }

    /**
     * Tests retrieving sales report with filtering.
     * Verifies proper report generation and data filtering.
     */
    @Test
    public void testGetSalesReport() {
        // Given
        ZonedDateTime startDate = ZonedDateTime.now().minusDays(7);
        ZonedDateTime endDate = ZonedDateTime.now();
        String clientName = "Test Client";
        
        List<SalesReportData> mockData = Arrays.asList(new SalesReportData("Test Client", 10L, 1000.0));
        when(salesReportDao.getSalesReport(startDate, endDate, clientName, 0, 10)).thenReturn(mockData);
        when(salesReportDao.countTotalClients(startDate, endDate, clientName)).thenReturn(1L);

        // When
        PaginatedResponse<SalesReportData> result = salesReportService.getSalesReport(startDate, endDate, clientName, 0, 10);

        // Then
        assertNotNull("Sales report should not be null", result);
        assertEquals("Should contain one report entry", 1, result.getContent().size());
        assertEquals("Total items should match", 1L, result.getTotalItems());
        verify(salesReportDao, times(1)).getSalesReport(startDate, endDate, clientName, 0, 10);
        verify(salesReportDao, times(1)).countTotalClients(startDate, endDate, clientName);
    }
} 