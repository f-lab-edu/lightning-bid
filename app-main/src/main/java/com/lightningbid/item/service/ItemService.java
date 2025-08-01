package com.lightningbid.item.service;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final CategoryService categoryService;

    private final AuctionService auctionService;

    private final ItemLikeService itemLikeService;

    @Transactional
    public ItemCreateResponseDto createItemWithAuction(ItemCreateRequestDto itemCreateRequestDto) {

        Duration duration;
        try {
            duration = Duration.parse(itemCreateRequestDto.getAuctionDuration());
        } catch (DateTimeParseException e) {
            // ISO 8601 형식이 아니면 예외 발생
            throw new IllegalArgumentException("유효하지 않은 기간 형식입니다. (입력: " + itemCreateRequestDto.getAuctionDuration() + ")");
        }

        // DTO to Entity
        Item item = Item.builder()
                .title(itemCreateRequestDto.getTitle())
                .userId(1L)
                .description(itemCreateRequestDto.getDescription())
                .categoryId(itemCreateRequestDto.getCategoryId())
                .categoryName(categoryService.findCategoryNameById(itemCreateRequestDto.getCategoryId()))
                .isDirectTrade(itemCreateRequestDto.getIsDirectTrade())
                .location(itemCreateRequestDto.getLocation())
                .build();
        Auction auction = Auction.builder()
                .startPrice(itemCreateRequestDto.getStartPrice())
                .currentBid(itemCreateRequestDto.getStartPrice())
                .instantSalePrice(itemCreateRequestDto.getInstantSalePrice())
                .auctionStartTime(LocalDateTime.now())
                .auctionEndTime(LocalDateTime.now().plus(duration))
                .item(item)
                .build();

        Auction resultAuction = auctionService.createAuction(auction);
        /*----------------------------------------*/
        // TODO: repository 개발 후 수정
//        Item resultItem = resultAuction.getItem();
        item.setViewCount(0);
        item.setId(1L);
        item.setStatus(ItemStatus.ACTIVE);
        item.setCreatedAt(LocalDateTime.now());
        Item resultItem = item;
        /*----------------------------------------*/

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
//                .bidUnit(resultAuction.getStartPrice())
                .seller(UserDto.builder()
                        .userId(1L)
                        .nickname("판매자_닉네임")
                        .profileImageUrl("https://...")
                        .build())
                .auctionStartTime(resultAuction.getAuctionStartTime())
                .auctionEndTime(resultAuction.getAuctionEndTime())
                .createdAt(resultItem.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public ItemResponseDto findItemWithAuctionByItemId(Long itemId) {
        Auction findAuction = auctionService.findAuctionByItemId(itemId);
        Item findItem = findAuction.getItem();

        boolean isLiked = itemLikeService.checkUserLikeStatus(1L, itemId);
        String categoryName = categoryService.findCategoryNameById(1L);

        // 조회수는 증가만 시킨다.
        increaseViewCount(itemId);

        boolean isDepositPaid = false;

        return ItemResponseDto.builder()
                .itemId(findItem.getId())
                .title(findItem.getTitle())
                .description(findItem.getDescription())
                .categoryId(findItem.getCategoryId())
                .categoryName(categoryName)
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
                        .userId(789L)
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


    @Async
    @Transactional
    public void increaseViewCount(Long itemId) {
        itemRepository.increaseViewCount(itemId);
    }
}
