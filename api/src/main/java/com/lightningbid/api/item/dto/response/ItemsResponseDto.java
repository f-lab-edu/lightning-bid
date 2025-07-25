package com.lightningbid.api.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemsResponseDto {

    private List<ItemSummaryDto> content;
    private PageInfoDto pageInfo;
}
