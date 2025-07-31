package com.lightningbid.auction.domain;

import com.lightningbid.item.item.domain.Item;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Auction {

    private BigDecimal startPrice;
    private BigDecimal currentBid;
    private BigDecimal instantSalePrice;
    private BigDecimal bidUnit;
    private Integer bidCount;
    private LocalDateTime auctionStartTime;
    private LocalDateTime auctionEndTime;
    private Item item; // 1:1, Auction이 연관 관계 주인 예정
}
