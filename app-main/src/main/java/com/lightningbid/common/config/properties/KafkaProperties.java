package com.lightningbid.common.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private final String bootstrapServers;

    private final Topics topics;

    private final Properties properties;

    @Getter @RequiredArgsConstructor
    public static class Topics {

        @NotBlank
        private final String bidNotificationTopic;

        @NotBlank
        private final String itemLikeTopic;
    }

    @Getter @RequiredArgsConstructor
    public static class Properties {

        private final Schema schema;

        @Getter @RequiredArgsConstructor
        public static class Schema {
            private final Registry registry;

            @Getter @RequiredArgsConstructor
            public static class Registry {

                private final String url;
            }
        }
    }
}
