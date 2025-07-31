package com.lightningbid.item.itemlike.repository;

import com.lightningbid.item.item.domain.Item;

public class ItemLikeRepository {

    public Item save(Item item) {

        return Item.builder().build();
    }

    public boolean existsByItemIdAndUserIdAndIsLiked(Long itemId, Long userId, boolean isLiked) {

        return false;
    }
}
