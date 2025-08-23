package com.lightningbid.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient tossPaymentsWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .build();
    }
}
