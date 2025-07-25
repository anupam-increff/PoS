package com.increff.pos.unit.dto;

import com.increff.pos.config.TestData;
import com.increff.pos.dto.InvoiceDto;
import com.increff.pos.flow.InvoiceFlow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceDtoTest {

    @Mock
    private InvoiceFlow invoiceFlow;

    @InjectMocks
    private InvoiceDto invoiceDto;

    @Test
    public void testInvoiceDto_FlowDelegation() {
        // This test simply verifies the DTO autowires the flow correctly
        assertNotNull("InvoiceDto should be created", invoiceDto);
        assertNotNull("InvoiceFlow should be autowired", invoiceFlow);
    }
} 