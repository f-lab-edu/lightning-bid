package com.lightningbid.api.item.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lightningbid.api.user.dto.response.UserDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateResponseDto {

    private Long itemId;

    private String title;

    private String description;

    private Long categoryId;

    private String categoryName;

    private List<String> imageIds;

    private List<String> imageUrls;

    private String status;

    private Boolean isDirectTrade;

    private String location;

    private Integer viewCount;

    private Integer startPrice;

    private Integer bidUnit;

    private UserDto seller;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auctionEndTime;
}
