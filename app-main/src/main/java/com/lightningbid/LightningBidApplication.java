package com.lightningbid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class
        , ManagementWebSecurityAutoConfiguration.class
        , DataSourceAutoConfiguration.class
})
public class LightningBidApplication {
    public static void main(String[] args) {
        SpringApplication.run(LightningBidApplication.class, args);
    }
}