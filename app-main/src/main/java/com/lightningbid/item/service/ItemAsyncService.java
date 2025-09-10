package com.lightningbid.item.service;

import com.lightningbid.item.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemAsyncService {

    private final ItemRepository itemRepository;

    @Async
    @Transactional
    public void increaseViewCount(Long itemId) {

        log.info("increaseViewCount() 현재 스레드: {}", Thread.currentThread().getName());
        itemRepository.increaseViewCount(itemId);
    }

}
