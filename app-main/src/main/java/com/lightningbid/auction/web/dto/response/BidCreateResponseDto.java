package com.lightningbid.auction.web.dto.response;

import com.lightningbid.item.domain.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BidCreateResponseDto {

    private Long bidId;
    private Long auctionId;
    private BigDecimal currentBid;
    private String itemStatus;
}

