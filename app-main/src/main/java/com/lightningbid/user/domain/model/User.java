package com.lightningbid.user.domain.model;

import com.lightningbid.auth.enums.Role;
import com.lightningbid.file.domain.model.File;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String name;

    private String providerId;

    private String provider;

    private String nickname;

    private String email;

    private String phone;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<File> file = new ArrayList<>();

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

    public void completeSignup(String nickname) {
        this.role = Role.ROLE_USER;
        this.nickname = nickname;
    }
}
