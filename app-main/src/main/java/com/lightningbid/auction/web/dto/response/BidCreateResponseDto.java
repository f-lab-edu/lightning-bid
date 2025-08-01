package com.lightningbid.auction.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BidCreateResponseDto {

    private Long bidId;
    private Long itemId;
    private Integer currentBid;
    private Integer bidCount;
}

