package com.lightningbid.api.payment.dto.response;

import com.lightningbid.api.user.dto.response.UserDto;
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
    private UserDto bidder;
}
