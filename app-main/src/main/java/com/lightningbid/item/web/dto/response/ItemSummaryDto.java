package com.lightningbid.item.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lightningbid.user.web.dto.response.UserResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemSummaryDto {

    private Long itemId;

    private Long auctionId;

    private String title;

    private String thumbnailUrl;

    private String location;

    private UserResponseDto seller;

    private BigDecimal price;

    private BigDecimal currentBid;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private int viewCount;

    private int likeCount;

    private int bidCount;
}
