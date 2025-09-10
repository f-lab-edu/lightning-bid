package com.lightningbid.auction.listener;

import com.lightningbid.auction.dto.NewBidderNotificationEventDto;
import com.lightningbid.auction.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.text.NumberFormat;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationEventListener {

    // 실제 이메일, SMS, 푸시 알림 등을 보내는 로직을 가진 서비스라고 가정합니다.
    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener
    public void handleNewBidEvent(NewBidderNotificationEventDto event) {

        NumberFormat numberFormatter = NumberFormat.getNumberInstance();
        String formattedAmount = numberFormatter.format(event.getBestBidAmount());

        String message = String.format(
                "[%s] 상품에 새로운 입찰가(%s원)가 등록되었습니다.",
                event.getItemTitle(),
                formattedAmount
        );

        log.info("handleNewBidEvent(): {}", message);

        notificationService.sendNewBidderNotification(event, message);
    }
}
