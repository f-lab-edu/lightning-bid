package com.lightningbid.user.domain.model;

import com.lightningbid.common.entity.BaseEntity;
import com.lightningbid.auth.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
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

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

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
