package com.lightningbid.item.infra.kafka.consumer;

import com.lightningbid.item.service.ItemLikeService;
import com.lightningbid.item.web.dto.request.ItemLikeEventDto;
import com.lightningbid.kafka.event.ItemLikeToggleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemLikeEventConsumer {

    private final ItemLikeService itemLikeService;

    @KafkaListener(
            topics = "${spring.kafka.topics.item-like-topic}",
            groupId = "item-like-toggle-processor",
            containerFactory = "itemLikeToggleEventKafkaListenerContainerFactory"
    )
    public void listenItemLikeToggleEvent(List<ItemLikeToggleEvent> events) { // 파라미터를 List로 받으면 배치처리 가능

        log.info(">>> item-like-toggle-processor event size: {}", events.size());
        itemLikeService.bulkUpsertItemLike(
                events.stream()
                        .map(event -> ItemLikeEventDto.builder()
                                .userId(event.getUserId())
                                .itemId(event.getItemId())
                                .build())
                        .distinct()
                        .toList()
        );
    }
}
