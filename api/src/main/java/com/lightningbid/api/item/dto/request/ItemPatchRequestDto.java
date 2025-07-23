package com.lightningbid.api.item.dto.request;

import lombok.*;

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
    private Integer startPrice;
    private LocalDateTime auctionEndTime;
}
