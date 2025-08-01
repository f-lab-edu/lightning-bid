package com.lightningbid.item.web.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemPatchRequestDto {

    private String title;
    private String description;
    private Long categoryId;
    private List<String> imageIds;
    private Boolean isDirectTrade;
    private String location;
    private BigDecimal startPrice;
    private LocalDateTime auctionEndTime;
}
