package com.lightningbid.auth.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class TokensDto {

    private String accessToken;
    private String refreshToken;
}
