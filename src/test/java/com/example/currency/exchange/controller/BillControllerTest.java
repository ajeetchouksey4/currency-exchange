/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.currency.exchange.handler.BillCalculationHandler;
import com.example.currency.exchange.model.request.BillRequest;
import com.example.currency.exchange.model.response.BillResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class BillControllerTest {

    @Mock
    private BillCalculationHandler billCalculationHandler;

    @InjectMocks
    private BillController billController;

    private BillRequest billRequest;

    @BeforeEach
    public void setup() {
        billRequest = new BillRequest();
        billRequest.setTotalAmount(new BigDecimal("1000"));
        billRequest.setTargetCurrency("USD");
        billRequest.setOriginalCurrency("EUR");
    }

    @Test
    @DisplayName("Given BillRequest, it should call handler layer and return 200: success")
    public void testCalculateBill_Success() {
        when(billCalculationHandler.calculateBill(any(BillRequest.class))).thenReturn(new BigDecimal("950"));

        ResponseEntity<BillResponse> response = billController.calculateBill(billRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("USD", response.getBody().getTargetCurrency());
        assertEquals(new BigDecimal("950"), response.getBody().getPayableAmount());

        verify(billCalculationHandler, times(1)).calculateBill(any(BillRequest.class));
    }
}
