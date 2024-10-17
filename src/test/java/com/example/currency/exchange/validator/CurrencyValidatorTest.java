/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.currency.exchange.enums.UserType;
import com.example.currency.exchange.model.Item;
import com.example.currency.exchange.model.request.BillRequest;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CurrencyValidatorTest {
    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ValidateCurrency ValidateCurrency;

    private CurrencyValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new CurrencyValidator();
    }

    @Test
    @DisplayName("Valid BillRequest with matching total amount and item prices")
    public void testValidBillRequest() {
        Item item1 = Item.builder()
                .name("Item1")
                .category("Electronics")
                .price(new BigDecimal("50.00"))
                .build();
        Item item2 = Item.builder()
                .name("Item2")
                .category("Books")
                .price(new BigDecimal("30.00"))
                .build();
        BillRequest request = BillRequest.builder()
                .totalAmount(new BigDecimal("80.00"))
                .originalCurrency("USD")
                .targetCurrency("INR")
                .user(UserType.EMPLOYEE)
                .tenure(12)
                .items(Arrays.asList(item1, item2))
                .build();

        boolean isValid = validator.isValid(request, context);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Invalid BillRequest when total amount is null")
    public void testInvalidTotalAmountWithNull() {
        Item item1 = Item.builder()
                .name("Item1")
                .category("Electronics")
                .price(new BigDecimal("50.00"))
                .build();
        BillRequest request = BillRequest.builder()
                .totalAmount(null)
                .originalCurrency("USD")
                .targetCurrency("INR")
                .user(UserType.CUSTOMER)
                .tenure(12)
                .items(Collections.singletonList(item1))
                .build();
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(violationBuilder);
        boolean isValid = validator.isValid(request, context);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Invalid BillRequest when items list is empty")
    public void testInvalidBillRequestWithEmptyItems() {
        BillRequest request = BillRequest.builder()
                .totalAmount(new BigDecimal("100.00"))
                .originalCurrency("USD")
                .targetCurrency("INR")
                .user(UserType.EMPLOYEE)
                .tenure(12)
                .items(Collections.emptyList())
                .build();
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(violationBuilder);
        boolean isValid = validator.isValid(request, context);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Invalid BillRequest when total amount does not match sum of item prices")
    public void testInvalidTotalAmountMismatch() {
        // Given a BillRequest where the total amount does not match the sum of item prices
        Item item1 = Item.builder()
                .name("Item1")
                .category("Electronics")
                .price(new BigDecimal("50.00"))
                .build();
        Item item2 = Item.builder()
                .name("Item2")
                .category("Books")
                .price(new BigDecimal("30.00"))
                .build();
        BillRequest request = BillRequest.builder()
                .totalAmount(new BigDecimal("100.00")) // Intentional mismatch
                .originalCurrency("USD")
                .targetCurrency("INR")
                .user(UserType.AFFILIATE)
                .tenure(12)
                .items(Arrays.asList(item1, item2))
                .build();
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(violationBuilder);
        boolean isValid = validator.isValid(request, context);

        // Then the request should be invalid
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Invalid BillRequest when total amount and items are both null")
    public void testNullItemsAndTotalAmount() {
        // Given a BillRequest with null total amount and null items
        BillRequest request = BillRequest.builder()
                .totalAmount(null)
                .originalCurrency("USD")
                .targetCurrency("INR")
                .user(UserType.AFFILIATE)
                .tenure(12)
                .items(null)
                .build();
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(violationBuilder);
        boolean isValid = validator.isValid(request, context);

        // Then the request should be invalid
        assertFalse(isValid);
    }
}
