package com.lightningbid.item.web.dto.request;

import lombok.*;

@EqualsAndHashCode
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemLikeEventDto {

    private Long userId;

    private Long itemId;
}
