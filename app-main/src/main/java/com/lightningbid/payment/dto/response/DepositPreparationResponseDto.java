package com.lightningbid.payment.dto.response;

import com.lightningbid.user.web.dto.response.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepositPreparationResponseDto {
    private String orderId;
    private Long itemId;
    private Integer amount;
    private UserResponseDto bidder;
}
