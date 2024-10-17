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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillCalculationService {

    @Value("${discounts.employee}")
    private BigDecimal employeeDiscountRate;

    @Value("${discounts.affiliate}")
    private BigDecimal affiliateDiscountRate;

    @Value("${discounts.loyalty}")
    private BigDecimal loyaltyDiscountRate;

    @Value("${discounts.tenure}")
    private Integer tenure;

    @Value("${discounts.flat-discount}")
    private BigDecimal flatDiscount;

    @Value("${discounts.flat-discount-threshold}")
    private BigDecimal flatDiscountThreshold;

    public BigDecimal applyDiscounts(BigDecimal totalAmount, UserType userType, List<Item> items, int customerYears) {
        // Step 1: Exclude percentage-based discounts for groceries
        BigDecimal nonGroceryTotal = calculateNonGroceryTotal(items);

        // Step 2: Apply percentage-based discount (if applicable)
        BigDecimal discountedAmount = applyPercentageDiscount(nonGroceryTotal, userType, customerYears);

        // Step 3: Apply flat discount based on the total amount
        discountedAmount = applyFlatDiscount(discountedAmount, totalAmount);

        return discountedAmount;
    }

    private BigDecimal calculateNonGroceryTotal(List<Item> items) {
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

        return nonGroceryTotal.subtract(discount).compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : nonGroceryTotal.subtract(discount);
    }

    private BigDecimal applyFlatDiscount(BigDecimal discountedAmount, BigDecimal totalAmount) {
        BigDecimal flatDiscountToApply =
                totalAmount.divide(flatDiscountThreshold, 0, RoundingMode.FLOOR).multiply(flatDiscount);

        //        return discountedAmount.subtract(flatDiscountToApply).compareTo(BigDecimal.ZERO) < 0
        //                ? BigDecimal.ZERO
        //                : discountedAmount.subtract(flatDiscountToApply);

        BigDecimal finalAmount = totalAmount.subtract(discountedAmount).subtract(flatDiscountToApply);

        return finalAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalAmount;
    }
}
