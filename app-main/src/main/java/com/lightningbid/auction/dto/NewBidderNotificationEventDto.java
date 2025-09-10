package com.lightningbid.auction.dto;

import lombok.*;

import java.math.BigDecimal;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewBidderNotificationEventDto {

    private BigDecimal bestBidAmount;

    private String itemTitle;

    private Long auctionId;

}
