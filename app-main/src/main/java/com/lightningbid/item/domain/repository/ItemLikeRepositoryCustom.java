package com.lightningbid.item.domain.repository;

import com.lightningbid.item.web.dto.request.ItemLikeEventDto;

import java.util.List;

public interface ItemLikeRepositoryCustom {

    void bulkUpsertAndToggle(List<ItemLikeEventDto> likeEvents);
}
