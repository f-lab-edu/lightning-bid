package com.lightningbid.common.config;

import com.lightningbid.item.web.dto.request.ItemLikeEventDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class LikeQueueConfig {

    @Bean
    public Queue<ItemLikeEventDto> likeEventQue() {
        return new ConcurrentLinkedQueue<>();
    }
}
