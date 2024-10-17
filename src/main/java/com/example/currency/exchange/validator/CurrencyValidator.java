/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.validator;

import com.example.currency.exchange.model.Item;
import com.example.currency.exchange.model.request.BillRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CurrencyValidator implements ConstraintValidator<ValidateCurrency, BillRequest> {
    @Override
    public void initialize(ValidateCurrency constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BillRequest request, ConstraintValidatorContext context) {
        List<String> violationMsg = new ArrayList<>();

        if (request.getTotalAmount() == null
                || request.getItems() == null
                || request.getItems().isEmpty()) {
            violationMsg.add("Total amount or items cannot be null or empty.");
        }
        if (request.getTotalAmount() != null && request.getItems() != null) {
            BigDecimal sumOfItemPrices =
                    request.getItems().stream().map(Item::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

            if (sumOfItemPrices.compareTo(request.getTotalAmount()) != 0) {
                violationMsg.add("Total amount does not match the sum of item prices.");
            }
        }
        if (!violationMsg.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(violationMsg.toString())
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
