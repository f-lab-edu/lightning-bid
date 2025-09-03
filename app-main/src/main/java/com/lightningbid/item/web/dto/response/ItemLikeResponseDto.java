package com.lightningbid.item.web.dto.response;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemLikeResponseDto {

    private Long itemId;

    private Long userId;

    private Boolean isLiked;
}
