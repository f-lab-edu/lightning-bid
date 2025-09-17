package com.lightningbid.auction.infra.kafka.consumer;

import com.lightningbid.auction.dto.NewBidderNotificationEventDto;
import com.lightningbid.auction.service.NotificationService;
import com.lightningbid.kafka.event.NewBidderNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;

@Slf4j
@RequiredArgsConstructor
@Service
public class BidEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topics.bid-notification-topic}",
            groupId = "bid-notification-processor",
            containerFactory = "newBidderEventKafkaListenerContainerFactory"
    )
    public void listenNewBidderEvent(NewBidderNotificationEvent event) {

        NumberFormat numberFormatter = NumberFormat.getNumberInstance();
        BigDecimal bestBidAmount = new BigDecimal(event.getBestBidAmount());
        String formattedAmount = numberFormatter.format(bestBidAmount);

        String message = String.format(
                "[%s] 상품에 새로운 입찰가(%s원)가 등록되었습니다.",
                event.getItemTitle(),
                formattedAmount
        );

        log.info("Kafka Consumer (BidEventConsumer): {}", message);

        NewBidderNotificationEventDto newBidderNotificationEventDto = NewBidderNotificationEventDto.builder()
                .bestBidAmount(bestBidAmount)
                .itemTitle(event.getItemTitle())
                .auctionId(event.getAuctionId())
                .build();

        notificationService.sendNewBidderNotification(newBidderNotificationEventDto, message);
    }
}