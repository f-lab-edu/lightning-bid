package com.lightningbid.item.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDto {

    private int size;
    private boolean hasNext;
    private CursorDto nextCursor;
}
