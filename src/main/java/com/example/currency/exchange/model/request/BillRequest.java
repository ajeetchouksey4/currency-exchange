/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.model.request;

import com.example.currency.exchange.enums.UserType;
import com.example.currency.exchange.model.Item;
import com.example.currency.exchange.validator.ValidateCurrency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidateCurrency
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BillRequest {
    private BigDecimal totalAmount;
    private String originalCurrency;
    private String targetCurrency;
    private UserType user;
    private Integer tenure;
    private List<Item> items;
}
