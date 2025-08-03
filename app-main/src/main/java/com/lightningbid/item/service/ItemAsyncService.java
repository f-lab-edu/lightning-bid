package com.lightningbid.item.service;

import com.lightningbid.item.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemAsyncService {

    private final ItemRepository itemRepository;

    @Async
    @Transactional
    public void increaseViewCount(Long itemId) {

        log.info("increaseViewCount() 현재 스레드: {}", Thread.currentThread().getName());
        itemRepository.increaseViewCount(itemId);
    }

}
