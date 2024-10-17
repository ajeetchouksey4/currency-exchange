/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "webclient")
public class WebClientProperties {

    private Map<String, ApiConfig> apis;

    @Data
    public static class ApiConfig {
        private String baseurl;
        private Services services;

        @Data
        public static class Services {
            private Exchange exchange;

            @Data
            public static class Exchange {
                private String url;
            }
        }
    }
}
