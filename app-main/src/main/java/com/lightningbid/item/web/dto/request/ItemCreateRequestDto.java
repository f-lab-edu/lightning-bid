package com.lightningbid.item.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateRequestDto {

    @NotNull(message = "게시글 제목은 필수 입력입니다.")
    @NotBlank(message = "게시글 제목은 필수 입력이며 공백일 수 없습니다.")
    private String title;

    private String description;

    @NotNull(message = "카테고리 설정은 필수입니다.")
    private Long categoryId;

    private List<String> imageIds;

    @NotNull(message = "직거래 여부는 필수입니다.")
    private Boolean isDirectTrade;

    private String location;

    @NotNull
    @Min(value = 0, message = "경매 시작 가격은 0원 이상이어야 합니다.")
    private BigDecimal startPrice;

    private BigDecimal instantSalePrice;

//    @NotNull
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private LocalDateTime auctionEndTime;

    @NotNull(message = "경매 기간은 필수 입력 사항 입니다.")
    @NotBlank(message = "경매 기간은 필수 입력이며 공백일 수 없습니다.")
    private String auctionDuration;
}
