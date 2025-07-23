package com.lightningbid.api.auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BidConfirmResponseDto {

    private Long itemId;
    private String status;
    private String sellerConfirmationStatus;
    private String buyerConfirmationStatus;
}
