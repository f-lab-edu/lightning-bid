package com.lightningbid.item.web.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemsRequestDto {
    private String seller;
    private String keyword;
    private String status;

    // 커서 페이징 파라미터
    private Long lastId;
    private LocalDateTime lastCreatedAt;
    private Integer lastViewCount;
    private Integer lastLikeCount;
}
