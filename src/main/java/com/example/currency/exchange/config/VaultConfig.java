/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.config;

import static com.example.currency.exchange.constants.CommonConstants.CURRENCY_EXCHANGE_API_KEY;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class VaultConfig {

    private Map<String, String> exchangeSecrets;

    @PostConstruct
    public void loadSecrets() throws IOException {
        ClassPathResource resource = new ClassPathResource("exchangesecrets.json");

        try (InputStream inputStream = resource.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            exchangeSecrets = objectMapper.readValue(inputStream, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            log.error("Error loading secrets from exchangesecrets.json", e);
            throw new RuntimeException("Failed to load secrets from exchangesecrets.json", e);
        }
    }

    public String getSecret() {
        return exchangeSecrets.get(CURRENCY_EXCHANGE_API_KEY);
    }
}
