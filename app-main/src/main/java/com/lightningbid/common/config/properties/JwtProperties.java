package com.lightningbid.common.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank
    private final String secret;

    @NotNull
    private final Long accessTokenExpirationMillis;

    @NotNull
    private final Long refreshTokenExpirationMillis;
}
