package com.lightningbid.item.item.service;

import com.lightningbid.item.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Async
    @Transactional
    public void increaseViewCount(Long itemId) {
        itemRepository.increaseViewCount(itemId);
    }
}
