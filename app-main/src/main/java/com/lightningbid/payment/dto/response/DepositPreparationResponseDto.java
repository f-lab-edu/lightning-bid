package com.lightningbid.payment.dto.response;

import com.lightningbid.user.dto.response.UserDto;
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
