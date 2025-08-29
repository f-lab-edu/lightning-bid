package com.lightningbid.payments.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TossConfirmRequestDto {

    private String paymentKey;

    private String orderId;

    private BigDecimal amount;
}
