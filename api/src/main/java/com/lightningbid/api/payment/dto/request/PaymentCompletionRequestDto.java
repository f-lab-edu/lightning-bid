package com.lightningbid.api.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompletionRequestDto {

    @NotNull
    private String paymentKey;

    @NotNull
    private String orderId;

    @NotNull
    private Integer amount;
}
