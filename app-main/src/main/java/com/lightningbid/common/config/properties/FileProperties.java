package com.lightningbid.common.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    private final Image image;

    @Getter @RequiredArgsConstructor
    public static class Image {

        @NotBlank
        private final String uploadDir;
    }
}
