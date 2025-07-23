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

    private List<String> imageIds;

    private List<String> imageUrls;

    private UserDto seller;

    private String title;

    private String description;

    private Long categoryId;

    private Boolean isDirectTrade;

    private String location;

    private Integer startPrice;

    //    private Integer currentBid;

    private Integer bidUnit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auctionEndTime;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private int viewCount;
}
