package com.lightningbid.auth.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignupTokenResponseDto {
    private String signupToken;
}
