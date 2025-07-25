package com.lightningbid.api.item.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lightningbid.api.user.dto.response.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ItemResponseDto {

    private Long itemId;

    private String title;

    private String description;

    private Long categoryId;

    private String categoryName;

    private List<String> imageUrls;

    private String status;

    private Boolean isDirectTrade;

    private String location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer viewCount;

    private Integer likeCount;

    private Integer chatCount;

    private Integer bidCount;

    private Boolean isLiked;

    private Boolean isDepositPaid;

    private UserDto seller;

    private AuctionDto auction;
}

