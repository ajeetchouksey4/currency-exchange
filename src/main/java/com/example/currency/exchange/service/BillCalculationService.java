/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.service;

import com.example.currency.exchange.enums.UserType;
import com.example.currency.exchange.model.Item;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillCalculationService {

    @Value("${discounts.employee}")
    private BigDecimal employeeDiscountRate;

    @Value("${discounts.affiliate}")
    private BigDecimal affiliateDiscountRate;

    @Value("${discounts.loyalty}")
    private BigDecimal loyaltyDiscountRate;

    @Value("${discounts.flat-discount}")
    private BigDecimal flatDiscount;

    @Value("${discounts.flat-discount-threshold}")
    private BigDecimal flatDiscountThreshold;

    public BigDecimal applyDiscounts(BigDecimal totalAmount, UserType userType, List<Item> items, int customerYears) {
        BigDecimal nonGroceryTotal = calculateNonGroceryTotal(items);

        BigDecimal discountedNonGroceryAmount = applyPercentageDiscount(nonGroceryTotal, userType, customerYears);

        BigDecimal finalDiscountedAmount = applyFlatDiscount(discountedNonGroceryAmount, totalAmount);

        return finalDiscountedAmount;
    }

    private BigDecimal calculateNonGroceryTotal(List<Item> items) {
        // Filter out groceries and sum up the prices
        return items.stream()
                .filter(item -> !"grocery".equalsIgnoreCase(item.getCategory()))
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal applyPercentageDiscount(BigDecimal nonGroceryTotal, UserType userType, int customerYears) {
        BigDecimal discount = BigDecimal.ZERO;

        if (userType == UserType.EMPLOYEE) {
            discount = nonGroceryTotal.multiply(employeeDiscountRate);
        } else if (userType == UserType.AFFILIATE) {
            discount = nonGroceryTotal.multiply(affiliateDiscountRate);
        } else if (customerYears > 2) {
            discount = nonGroceryTotal.multiply(loyaltyDiscountRate);
        }

        // Apply the percentage discount and return the remaining amount
        BigDecimal discountedAmount = nonGroceryTotal.subtract(discount);
        return discountedAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discountedAmount;
    }

    private BigDecimal applyFlatDiscount(BigDecimal discountedAmount, BigDecimal totalAmount) {
        // Calculate flat discount based on total bill, not just non-groceries
        BigDecimal flatDiscountToApply =
                totalAmount.divide(flatDiscountThreshold, 0, RoundingMode.FLOOR).multiply(flatDiscount);

        // Apply the flat discount on top of the percentage-based discount
        BigDecimal finalAmount = totalAmount
                .subtract(
                        discountedAmount.compareTo(BigDecimal.ZERO) <= 0
                                ? BigDecimal.ZERO
                                : totalAmount.subtract(discountedAmount))
                .subtract(flatDiscountToApply);

        // Ensure the final amount is non-negative
        return finalAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalAmount;
    }
}
