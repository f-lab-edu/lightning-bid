package com.lightningbid.file.domain.model;

import com.lightningbid.common.entity.BaseEntity;
import com.lightningbid.file.domain.enums.FileDomain;
import com.lightningbid.user.domain.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String uuid;

    @Enumerated(EnumType.STRING)
    private FileDomain domain;

    private String originalFileName;

    private String storedFileName;

    private String fileUrl;

    private String mimeType;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
