/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.handler;

import com.example.currency.exchange.model.request.BillRequest;
import com.example.currency.exchange.service.BillCalculationService;
import com.example.currency.exchange.webclient.CurrencyExchangeClient;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillCalculationHandler {
    private final CurrencyExchangeClient exchangeClient;
    private final BillCalculationService billCalculationService;

    public BigDecimal calculateBill(BillRequest request) {
        BigDecimal totalAmount = request.getTotalAmount();

        // Step 1: Apply Discounts
        BigDecimal discountedAmount = billCalculationService.applyDiscounts(
                totalAmount, request.getUser(), request.getItems(), request.getTenure());

        // Step 2: Fetch Currency Exchange Rates
        Map<String, BigDecimal> rates = exchangeClient.getExchangeRates(request.getOriginalCurrency());
        Optional<BigDecimal> exchangeRate = Optional.ofNullable(rates.get(request.getTargetCurrency()));
        if (!exchangeRate.isPresent()) {
            log.error("Exchange rate for target currency {} not found.", request.getTargetCurrency());
            throw new IllegalArgumentException(
                    "Exchange rate for target currency " + request.getTargetCurrency() + " not found.");
        }

        // Step 3: Convert the discounted amount to the target currency
        BigDecimal payableAmountInTargetCurrency = discountedAmount.multiply(exchangeRate.get());

        // Step 4: Return the final payable amount
        return payableAmountInTargetCurrency.setScale(2, RoundingMode.HALF_UP);
    }
}
