/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.config;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    @Bean
    public Map<String, WebClient> webClients(WebClientProperties webClientProperties) {
        return webClientProperties.getApis().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> createWebClient(entry.getValue())));
    }

    private WebClient createWebClient(WebClientProperties.ApiConfig apiConfig) {
        return WebClient.builder().baseUrl(apiConfig.getBaseurl()).build();
    }
}
