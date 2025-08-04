package com.lightningbid.item.domain.repository;


import com.lightningbid.item.domain.model.Item;

public class ItemLikeRepository {

    public Item save(Item item) {

        return Item.builder().build();
    }

    public boolean existsByItemIdAndUserIdAndIsLiked(Long itemId, Long userId, boolean isLiked) {

        return false;
    }
}
