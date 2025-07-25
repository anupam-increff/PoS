package com.increff.pos.unit.service;

import com.increff.pos.config.TestData;
import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.DaySalesService;
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
public class DaySalesServiceTest {

    @Mock
    private DaySalesDao daySalesDao;

    @InjectMocks
    private DaySalesService daySalesService;

    private DaySalesPojo testDaySales;
    private ZonedDateTime testDate;

    @Before
    public void setUp() {
        testDate = ZonedDateTime.now();
        testDaySales = TestData.daySales(testDate);
        testDaySales.setInvoicedOrdersCount(5);
        testDaySales.setInvoicedItemsCount(25);
        testDaySales.setTotalRevenue(500.0);
    }

    @Test
    public void testInsert_Success() {
        // Given
        doNothing().when(daySalesDao).insert(any(DaySalesPojo.class));

        // When
        daySalesService.insert(testDaySales);

        // Then
        verify(daySalesDao, times(1)).insert(testDaySales);
    }

    @Test
    public void testGetByDate_Success() {
        // Given
        when(daySalesDao.getReportForDate(testDate)).thenReturn(testDaySales);

        // When
        DaySalesPojo result = daySalesService.getByDate(testDate);

        // Then
        assertEquals(testDaySales, result);
        verify(daySalesDao, times(1)).getReportForDate(testDate);
    }

    @Test
    public void testGetByDate_NotFound() {
        // Given
        when(daySalesDao.getReportForDate(testDate)).thenReturn(null);

        // When
        DaySalesPojo result = daySalesService.getByDate(testDate);

        // Then
        assertNull(result);
        verify(daySalesDao, times(1)).getReportForDate(testDate);
    }

    @Test
    public void testGetBetween_Success() {
        // Given
        ZonedDateTime startDate = testDate.minusDays(7);
        ZonedDateTime endDate = testDate;
        List<DaySalesPojo> salesList = Arrays.asList(testDaySales);
        when(daySalesDao.getReportBetweenDates(startDate, endDate)).thenReturn(salesList);

        // When
        List<DaySalesPojo> result = daySalesService.getBetween(startDate, endDate);

        // Then
        assertEquals(salesList, result);
        verify(daySalesDao, times(1)).getReportBetweenDates(startDate, endDate);
    }

    @Test
    public void testGetBetween_EmptyResult() {
        // Given
        ZonedDateTime startDate = testDate.minusDays(7);
        ZonedDateTime endDate = testDate;
        when(daySalesDao.getReportBetweenDates(startDate, endDate)).thenReturn(Arrays.asList());

        // When
        List<DaySalesPojo> result = daySalesService.getBetween(startDate, endDate);

        // Then
        assertEquals(0, result.size());
        verify(daySalesDao, times(1)).getReportBetweenDates(startDate, endDate);
    }
} 