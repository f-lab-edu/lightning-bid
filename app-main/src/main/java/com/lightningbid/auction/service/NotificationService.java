package com.lightningbid.auction.service;

import com.lightningbid.auction.dto.NewBidderNotificationEventDto;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNewBidderNotification(NewBidderNotificationEventDto newBidderNotificationEventDto, String message) {
        // TODO 알림 전송
    }
}
