package com.lightningbid.item.service;

import com.lightningbid.item.domain.model.ItemLike;
import com.lightningbid.item.domain.repository.ItemLikeRepository;
import com.lightningbid.item.domain.repository.ItemRepository;
import com.lightningbid.item.web.dto.request.ItemLikeEventDto;
import com.lightningbid.item.web.dto.response.ItemLikeResponseDto;
import com.lightningbid.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@RequiredArgsConstructor
@Service
public class ItemLikeService {

    private final ItemLikeRepository itemLikeRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final Queue<ItemLikeEventDto> likeEventQue;

    private final ReentrantLock lock = new ReentrantLock();

    @Transactional(readOnly = true)
    public boolean checkItemLikeStatus(Long userId, Long itemId) {

        return itemLikeRepository.findByUserIdAndItemId(userId, itemId)
                .map(ItemLike::getIsLiked)
                .orElse(false);
    }

    @Transactional
    public ItemLikeResponseDto likeItem(Long userId, Long itemId) {

        log.info("3. @Transactional 메소드 진입, 스레드: {}", Thread.currentThread().getName());
        ItemLike itemLike =
                itemLikeRepository.findByUserIdAndItemId(userId, itemId)
                        .map(i -> {
                            i.toggleLikeStatus();
                            return i;
                        })
                        .orElseGet(() -> itemLikeRepository.save(ItemLike.builder()
                                .isLiked(true)
                                .user(userRepository.getReferenceById(userId))
                                .item(itemRepository.getReferenceById(itemId))
                                .build())
                        );

        return ItemLikeResponseDto.builder()
                .userId(itemLike.getUser().getId())
                .itemId(itemLike.getItem().getId())
                .isLiked(itemLike.getIsLiked())
                .build();
    }

    public void processQueue() {

        // 락 즉시 획득 시도
        if (!lock.tryLock()) return;

        try {
            while (!likeEventQue.isEmpty()) { // 로직 처리 중 큐에 데이터가 다시 생길시를 대비

                // 최종 상태만 남긴다.
                Set<String> finalLikeStatusMap = new HashSet<>();
                while (!likeEventQue.isEmpty()) {
                    ItemLikeEventDto likeEvent = likeEventQue.poll();
                    finalLikeStatusMap.add(likeEvent.getUserId() + ":" + likeEvent.getItemId());
                }

                List<ItemLikeEventDto> likeToUpsert = new ArrayList<>();

                for (String key : finalLikeStatusMap) {
                    String[] ids = key.split(":");

                    likeToUpsert.add(ItemLikeEventDto.builder()
                            .userId(Long.parseLong(ids[0]))
                            .itemId(Long.parseLong(ids[1]))
                            .build());
                }

                itemLikeRepository.bulkUpsertAndToggle(likeToUpsert);
            }

        } finally {
            lock.unlock(); // 모든 작업 후 반드시 락 해제
        }
    }
}
