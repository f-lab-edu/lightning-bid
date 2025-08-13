package com.lightningbid.item.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lightningbid.user.web.dto.response.UserResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateResponseDto {

    private Long itemId;

    private Long auctionId;

    private String title;

    private String description;

    private Long categoryId;

    private String categoryName;

    private List<String> imageIds;

    private List<String> imageUrls;

    private String status;

    private Boolean isDirectTrade;

    private String location;

    private BigDecimal startPrice;

    private BigDecimal instantSalePrice;

    private BigDecimal bidUnit;

    private UserResponseDto seller;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auctionStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auctionEndTime;
}
