package com.lightningbid.item.service;

import com.lightningbid.item.web.dto.response.ItemLikeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Service
public class ItemLikeAsyncService {


    private final ItemLikeService itemLikeService;

    private final Executor taskExecutor;

    public CompletableFuture<ItemLikeResponseDto> likeItemAsync(Long userId, Long itemId) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("2. supplyAsync() 진입, 스레드: {}", Thread.currentThread().getName());
            // 이 람다 블록 안의 코드가 비동기 스레드 풀의 스레드에서 실행된다.
            return itemLikeService.likeItem(userId, itemId);
        }, taskExecutor);
    }

    @Async
    public void triggerProcessing() {

        itemLikeService.processQueue();
    }
}
