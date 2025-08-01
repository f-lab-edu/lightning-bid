package com.lightningbid.item.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lightningbid.user.dto.response.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemSummaryDto {

    private Long itemId;

    private String title;

    private String thumbnailUrl;

    private String location;

    private UserDto seller;

    private int price; // 즉시 구매가 또는 경매 시작가

    private Integer currentBid; // 현재 입찰가 (null일 수 있으므로 Integer 사용)

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private int viewCount;

    private int likeCount;

    private int bidCount;
}
