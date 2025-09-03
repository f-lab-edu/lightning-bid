package com.lightningbid.item.service;

import com.lightningbid.item.domain.model.ItemLike;
import com.lightningbid.item.domain.repository.ItemLikeRepository;
import com.lightningbid.item.domain.repository.ItemRepository;
import com.lightningbid.item.web.dto.response.ItemLikeResponseDto;
import com.lightningbid.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class ItemLikeService {

    private final ItemLikeRepository itemLikeRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;


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
}
