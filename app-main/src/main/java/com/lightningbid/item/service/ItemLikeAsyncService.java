package com.lightningbid.item.service;

import com.lightningbid.item.web.dto.response.ItemLikeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Service
public class ItemLikeAsyncService {


    private final ItemLikeService itemLikeService;


    @Async
    public CompletableFuture<ItemLikeResponseDto> likeItemAsync(Long userId, Long itemId) {

        log.info("2. @Async 메소드 진입, 스레드: {}", Thread.currentThread().getName());
        return CompletableFuture.completedFuture(itemLikeService.likeItem(userId, itemId));
    }
}
