package com.lightningbid.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CursorResult<T> {

    private List<T> content; // 데이터 목록
    private Boolean hasNext;   // 다음 페이지 존재 여부
    private String nextCursor; // 다음 페이지를 요청할 커서 (문자열로 조합)
}
