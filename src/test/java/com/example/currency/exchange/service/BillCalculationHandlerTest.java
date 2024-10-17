/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.currency.exchange.enums.UserType;
import com.example.currency.exchange.handler.BillCalculationHandler;
import com.example.currency.exchange.model.request.BillRequest;
import com.example.currency.exchange.webclient.CurrencyExchangeClient;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BillCalculationHandlerTest {

    @Mock
    private CurrencyExchangeClient exchangeClient;

    @Mock
    private BillCalculationService billCalculationService;

    @InjectMocks
    private BillCalculationHandler billCalculationHandler;

    private BillRequest billRequest;

    @BeforeEach
    public void setup() {
        billRequest = new BillRequest();
        billRequest.setTotalAmount(new BigDecimal("1000"));
        billRequest.setOriginalCurrency("EUR");
        billRequest.setTargetCurrency("USD");

        billRequest.setUser(UserType.EMPLOYEE);
        billRequest.setItems(null);
        billRequest.setTenure(3);
    }

    @Test
    @DisplayName("Test Success: Should Calculate Final Bill Amount Correctly")
    public void testCalculateBill_Success() {
        when(billCalculationService.applyDiscounts(any(BigDecimal.class), any(), any(), anyInt()))
                .thenReturn(new BigDecimal("800"));

        Map<String, BigDecimal> exchangeRates = new HashMap<>();
        exchangeRates.put("USD", new BigDecimal("1.2"));
        when(exchangeClient.getExchangeRates("EUR")).thenReturn(exchangeRates);

        BigDecimal result = billCalculationHandler.calculateBill(billRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("960.00"), result);

        verify(billCalculationService, times(1)).applyDiscounts(any(BigDecimal.class), any(), any(), anyInt());
        verify(exchangeClient, times(1)).getExchangeRates("EUR");
    }

    @Test
    @DisplayName("Test Failure: Should Throw Exception When Currency Exchange Rate Is Not Found")
    public void testCalculateBill_NoExchangeRateFound() {
        when(billCalculationService.applyDiscounts(any(BigDecimal.class), any(), any(), anyInt()))
                .thenReturn(new BigDecimal("800"));

        Map<String, BigDecimal> exchangeRates = new HashMap<>();
        exchangeRates.put("INR", BigDecimal.valueOf(80.88));
        when(exchangeClient.getExchangeRates("EUR")).thenReturn(exchangeRates);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            billCalculationHandler.calculateBill(billRequest);
        });

        assertEquals("Exchange rate for target currency USD not found.", exception.getMessage());

        verify(billCalculationService, times(1)).applyDiscounts(any(BigDecimal.class), any(), any(), anyInt());
        verify(exchangeClient, times(1)).getExchangeRates("EUR");
    }

    @Test
    @DisplayName("Test Failure: Should Throw Exception When Discount Application Fails")
    public void testCalculateBill_ApplyDiscountFailure() {
        when(billCalculationService.applyDiscounts(any(BigDecimal.class), any(), any(), anyInt()))
                .thenThrow(new RuntimeException("Error applying discounts"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            billCalculationHandler.calculateBill(billRequest);
        });

        assertEquals("Error applying discounts", exception.getMessage());

        verify(billCalculationService, times(1)).applyDiscounts(any(BigDecimal.class), any(), any(), anyInt());
        verify(exchangeClient, never()).getExchangeRates(anyString());
    }
}
