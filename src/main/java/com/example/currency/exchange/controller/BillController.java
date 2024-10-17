/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.controller;

import com.example.currency.exchange.handler.BillCalculationHandler;
import com.example.currency.exchange.model.request.BillRequest;
import com.example.currency.exchange.model.response.BillResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BillController {
    private final BillCalculationHandler billCalculationHandler;

    @Operation(summary = "Calculate the total payable amount after applying discounts and currency conversion")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully calculated payable amount"),
                @ApiResponse(responseCode = "404", description = "Currency code not found"),
                @ApiResponse(
                        responseCode = "500",
                        description = "An Internal server error occurred while calculating conversion")
            })
    @PostMapping("/calculate")
    public ResponseEntity<BillResponse> calculateBill(@Valid @RequestBody BillRequest billRequest) {
        BigDecimal payableAmount = billCalculationHandler.calculateBill(billRequest);
        BillResponse response = new BillResponse(payableAmount, billRequest.getTargetCurrency());
        return ResponseEntity.ok(response);
    }
}
