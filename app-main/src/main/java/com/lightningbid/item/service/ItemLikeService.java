package com.lightningbid.item.service;

import com.lightningbid.item.domain.repository.ItemLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemLikeService {

    ItemLikeRepository itemLikeRepository = new ItemLikeRepository();

    @Transactional(readOnly = true)
    public boolean checkUserLikeStatus(Long userId, Long itemId) {

        return itemLikeRepository.existsByItemIdAndUserIdAndIsLiked(userId, itemId, true);
    }

}
