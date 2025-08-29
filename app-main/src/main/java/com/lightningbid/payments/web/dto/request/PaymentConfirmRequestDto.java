package com.lightningbid.payments.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PaymentConfirmRequestDto {

    @NotNull
    private String paymentKey;

    @NotNull
        private String orderId;

    @NotNull
    private BigDecimal amount;
}
