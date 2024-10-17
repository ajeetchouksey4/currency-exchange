/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.webclient;

import com.example.currency.exchange.config.VaultConfig;
import com.example.currency.exchange.config.WebClientProperties;
import com.example.currency.exchange.model.response.ExchangeRateResponse;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CurrencyExchangeClient {
    private final Map<String, WebClient> webClients;
    private final WebClientProperties webClientProperties;
    private final VaultConfig vaultConfig;

    private static final String API_NAME = "currencyexchange";

    @Cacheable(value = "exchangePriceCache")
    public Map<String, BigDecimal> getExchangeRates(String currencyType) {
        WebClient webClient = webClients.get(API_NAME);

        if (webClient == null) {
            throw new IllegalArgumentException("API not configured: " + API_NAME);
        }
        String baseUrl = webClientProperties.getApis().get(API_NAME).getBaseurl();
        String apiEndpoint = webClientProperties
                .getApis()
                .get(API_NAME)
                .getServices()
                .getExchange()
                .getUrl();

        String urlTemplate = baseUrl
                + apiEndpoint.replace("{api_key}", vaultConfig.getSecret()).replace("{currency_type}", currencyType);
        return webClient
                .get()
                .uri(urlTemplate)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ClientResponse::createException)
                .bodyToMono(ExchangeRateResponse.class)
                .map(ExchangeRateResponse::getRates)
                .block();
    }
}
