package com.lightningbid.payments.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PaymentReadyRequestDto {

    @NotNull(message = "auctionId 는 필수 입니다.")
    private Long auctionId;
}
