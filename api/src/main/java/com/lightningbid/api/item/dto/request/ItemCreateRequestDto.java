package com.lightningbid.api.item.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateRequestDto {

    @NotNull
    private String title;

    private String description;

    @NotNull
    private Long categoryId;

    private List<String> imageIds;

    @NotNull
    private Boolean isDirectTrade;

    private String location;

    @NotNull
    private Integer startPrice;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auctionEndTime;
}
