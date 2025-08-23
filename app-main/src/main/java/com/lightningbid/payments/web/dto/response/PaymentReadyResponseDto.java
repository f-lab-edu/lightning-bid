package com.lightningbid.payments.web.dto.response;

import com.lightningbid.payments.domain.enums.PaymentsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReadyResponseDto {
    
    private BigDecimal amount;

    private String orderId;
    
    private PaymentsStatus status;
}
