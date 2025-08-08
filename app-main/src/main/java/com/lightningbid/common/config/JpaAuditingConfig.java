package com.lightningbid.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
