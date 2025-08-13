package com.lightningbid.item.domain.repository;

import com.lightningbid.common.dto.CursorResult;
import com.lightningbid.item.web.dto.response.ItemSummaryDto;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    CursorResult<ItemSummaryDto> search(String title, String sortColumn, String sortDirection, String cursor, Pageable pageable);
}
