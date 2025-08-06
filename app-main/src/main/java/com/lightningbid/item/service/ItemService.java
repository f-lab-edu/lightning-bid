package com.lightningbid.item.service;

import com.lightningbid.auction.domain.exception.ItemNotFoundException;
import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.auction.service.AuctionService;
import com.lightningbid.item.domain.enums.ItemStatus;
import com.lightningbid.item.domain.model.Item;
import com.lightningbid.item.domain.repository.ItemRepository;
import com.lightningbid.item.web.dto.request.ItemCreateRequestDto;
import com.lightningbid.item.web.dto.response.AuctionDto;
import com.lightningbid.item.web.dto.response.ItemCreateResponseDto;
import com.lightningbid.item.web.dto.response.ItemResponseDto;
import com.lightningbid.user.dto.response.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemAsyncService itemAsyncService;

    private final CategoryService categoryService;

    private final AuctionService auctionService;

    private final ItemLikeService itemLikeService;

    @Transactional
    public ItemCreateResponseDto createItemWithAuction(ItemCreateRequestDto itemCreateRequestDto, Duration auctionDuration) {
        // TODO 이미지 처리

        LocalDateTime auctionStartTime;

        // DTO to Entity
        Auction auction = Auction.builder()
                .startPrice(itemCreateRequestDto.getStartPrice())
                .currentBid(itemCreateRequestDto.getStartPrice())
                .instantSalePrice(itemCreateRequestDto.getInstantSalePrice())
                .auctionStartTime(auctionStartTime = LocalDateTime.now())
                .auctionEndTime(auctionStartTime.plus(auctionDuration))
                .item(Item.builder()
                        .title(itemCreateRequestDto.getTitle())
                        .userId(1L)
                        .description(itemCreateRequestDto.getDescription())
                        .categoryId(itemCreateRequestDto.getCategoryId())
                        .categoryName(categoryService.findCategoryNameById(itemCreateRequestDto.getCategoryId()))
                        .status(ItemStatus.ACTIVE)
                        .isDirectTrade(itemCreateRequestDto.getIsDirectTrade())
                        .location(itemCreateRequestDto.getLocation())
                        .build())
                .build();

        Auction resultAuction = auctionService.createAuction(auction);
        Item resultItem = resultAuction.getItem();

        return ItemCreateResponseDto.builder()
                .itemId(resultItem.getId())
                .title(resultItem.getTitle())
                .description(resultItem.getDescription())
                .categoryId(resultItem.getCategoryId())
                .categoryName(resultItem.getCategoryName())
                .status(resultItem.getStatus().getCode())
                .isDirectTrade(resultItem.getIsDirectTrade())

                .imageIds(itemCreateRequestDto.getImageIds())
                .imageUrls(List.of("https://...", "https://..."))

                .location(resultItem.getLocation())
                .startPrice(resultAuction.getStartPrice())
                .bidUnit(resultAuction.getBidUnit())
                .seller(UserDto.builder()
                        .userId(1L)
                        .nickname("판매자_닉네임")
                        .profileImageUrl("https://...")
                        .build())
                .auctionStartTime(resultAuction.getAuctionStartTime())
                .auctionEndTime(resultAuction.getAuctionEndTime())
                .build();
    }

    @Transactional(readOnly = true)
    public ItemResponseDto findItemWithAuctionByItemId(Long itemId) {

        log.info("findItemWithAuctionByItemId() 현재 스레드: {}", Thread.currentThread().getName());

        Item findItem = itemRepository.findWithAuctionById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("조회된 상품이 없습니다. (입력: " + itemId + ")" ));
        Auction findAuction = findItem.getAuction();

        boolean isLiked = itemLikeService.checkUserLikeStatus(1L, itemId);

        // 조회수는 별도의 스레드에서 증가만 시킨다.
        itemAsyncService.increaseViewCount(itemId);

        // TODO 보증급 납부여부 개발
        boolean isDepositPaid = false;

        return ItemResponseDto.builder()
                .itemId(findItem.getId())
                .title(findItem.getTitle())
                .description(findItem.getDescription())
                .categoryId(findItem.getCategoryId())
                .categoryName(categoryService.findCategoryNameById(findItem.getCategoryId()))
                .imageIds(List.of("1", "2", "3"))
                .imageUrls(List.of(
                        "https://...",
                        "https://...",
                        "https://..."
                ))
                .status(findItem.getStatus().getCode())
                .isDirectTrade(findItem.getIsDirectTrade())
                .location(findItem.getLocation())
                .viewCount(findItem.getViewCount() + 1)
                .likeCount(findItem.getLikeCount())
                .chatCount(findItem.getChatCount())
                .isLiked(isLiked)
                .isDepositPaid(isDepositPaid)
                .seller(UserDto.builder()
                        .userId(1L)
                        .nickname("판매자_닉네임")
                        .profileImageUrl("https://...")
                        .build())
                .auction(AuctionDto.builder()
                        .startPrice(findAuction.getStartPrice())
                        .currentBid(findAuction.getCurrentBid())
                        .bidUnit(findAuction.getBidUnit())
                        .bidCount(findAuction.getBidCount())
                        .auctionStartTime(findAuction.getAuctionStartTime())
                        .auctionEndTime(findAuction.getAuctionEndTime())
                        .build())
                .build();
    }
}
