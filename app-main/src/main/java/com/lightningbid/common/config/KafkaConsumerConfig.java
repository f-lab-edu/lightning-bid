package com.lightningbid.common.config;

import com.lightningbid.common.config.properties.KafkaProperties;
import com.lightningbid.kafka.event.ItemLikeToggleEvent;
import com.lightningbid.kafka.event.NewBidderNotificationEvent;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
    public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ItemLikeToggleEvent> itemLikeToggleEventKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ItemLikeToggleEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(ItemLikeToggleEvent.class));
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NewBidderNotificationEvent> newBidderEventKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, NewBidderNotificationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(NewBidderNotificationEvent.class));
        return factory;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> messageType) {
        // application.yml의 spring.kafka.consumer.* 설정은 Spring Boot가 자동으로 생성하는 기본(default) 팩토리에만 적용되고,
        // 이제 다음과 같이 커스텀 팩토리 사용하니 yml 설정은 무시 된다.

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        props.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaProperties.getProperties().getSchema().getRegistry().getUrl());
        props.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, messageType.getName());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // 컨슈머가 한 번에 poll() 할 때 최대로 가져올 최대 메시지 수 (기본값: 500)
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);

        return new DefaultKafkaConsumerFactory<>(props);
    }
}
