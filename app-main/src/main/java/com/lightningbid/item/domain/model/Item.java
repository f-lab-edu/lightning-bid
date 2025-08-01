package com.lightningbid.item.domain.model;

import com.lightningbid.item.domain.enums.ItemStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    Long id;

    Long userId;

    String title;

    String description;

    Long categoryId;

    String categoryName;

    @Enumerated(EnumType.STRING)
    ItemStatus status;

    Boolean isDirectTrade;

    String location;

    Integer viewCount;

    Integer likeCount;

    Integer chatCount;

    LocalDateTime createdAt;
}
