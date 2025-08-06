package com.lightningbid.user.domain.model;

import com.lightningbid.common.entity.BaseEntity;
import com.lightningbid.auth.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String name;

    private String providerId;

    private String provider;

    private String nickname;

    private String email;

    private String phone;

    private String profileUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void updateOAuthInfo(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public void completeSignup(String nickname, String profileUrl) {
        this.role = Role.ROLE_USER;
        this.nickname = nickname;
//        this.profileUrl = profileUrl;
    }
}
