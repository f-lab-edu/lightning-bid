package com.lightningbid.auth.dto;

import com.lightningbid.auth.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {

    private Long id;
    private String name;
    private String username;
    private String nickname;
    private String profileUrl;
    private Role role;
}
