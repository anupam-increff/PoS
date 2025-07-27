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

    @Test
    public void testGetSalesReport_Success() {
        // Given
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        String clientName = "Test Client";
        List<SalesReportData> reports = Arrays.asList(testSalesReport);
        when(salesReportDao.getSalesReport(start, end, clientName, 0, 10)).thenReturn(reports);
        when(salesReportDao.countTotalClients(start, end, clientName)).thenReturn(1L);

        // When
        PaginatedResponse<SalesReportData> result = salesReportService.getSalesReport(start, end, clientName, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Client", result.getContent().get(0).getClient());
        assertEquals(Long.valueOf(25L), result.getContent().get(0).getQuantity());
        assertEquals(Double.valueOf(1500.0), result.getContent().get(0).getRevenue());
        verify(salesReportDao, times(1)).getSalesReport(start, end, clientName, 0, 10);
        verify(salesReportDao, times(1)).countTotalClients(start, end, clientName);
    }
} 