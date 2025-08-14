package com.lightningbid.item.service;

import com.lightningbid.auction.domain.exception.ItemNotFoundException;
import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.auction.service.AuctionService;
import com.lightningbid.auth.dto.CustomOAuth2User;
import com.lightningbid.common.dto.CursorResult;
import com.lightningbid.file.domain.model.File;
import com.lightningbid.file.service.FileService;
import com.lightningbid.item.domain.enums.ItemStatus;
import com.lightningbid.item.domain.model.Item;
import com.lightningbid.item.domain.repository.ItemRepository;
import com.lightningbid.item.web.dto.request.ItemCreateRequestDto;
import com.lightningbid.item.web.dto.response.AuctionDto;
import com.lightningbid.item.web.dto.response.ItemCreateResponseDto;
import com.lightningbid.item.web.dto.response.ItemResponseDto;
import com.lightningbid.item.web.dto.response.ItemSummaryDto;
import com.lightningbid.user.domain.model.User;
import com.lightningbid.user.service.UserService;
import com.lightningbid.user.web.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemAsyncService itemAsyncService;

    private final ItemLikeService itemLikeService;

    private final CategoryService categoryService;

    private final AuctionService auctionService;

    private final UserService userService;

    private final FileService fileService;

    @Transactional
    public ItemCreateResponseDto createItemWithAuction(ItemCreateRequestDto itemCreateRequestDto, Duration auctionDuration, CustomOAuth2User user) {
        // TODO 이미지 처리

        LocalDateTime auctionStartTime;

        // DTO to Entity
        Auction auction = Auction.builder()
                .startPrice(itemCreateRequestDto.getStartPrice())
                .currentBid(itemCreateRequestDto.getStartPrice())
                .instantSalePrice(itemCreateRequestDto.getInstantSalePrice())
                .auctionStartTime(auctionStartTime = LocalDateTime.now())
                .auctionEndTime(auctionStartTime.plus(auctionDuration))
                .bidUnit(itemCreateRequestDto.getBidUnit())
                .item(Item.builder()
                        .title(itemCreateRequestDto.getTitle())
                        .user(User.builder().id(user.getId()).build())
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

        fileService.updateFileItemIdByUuidIn(resultItem.getId(), user.getId(), itemCreateRequestDto.getImageIds());

        List<File> files = fileService.findFilesByItemIdOrUserId(resultItem.getId(), user.getId());
        String profileImageUrl = null;
        List<String> itemImageUrls = new ArrayList<>();
        for (File file : files) {
            if (file.getItem() == null && profileImageUrl == null)
                profileImageUrl = file.getFileUrl();
            else
                itemImageUrls.add(file.getFileUrl());
        }


        return ItemCreateResponseDto.builder()
                .itemId(resultItem.getId())
                .auctionId(resultAuction.getId())
                .title(resultItem.getTitle())
                .description(resultItem.getDescription())
                .categoryId(resultItem.getCategoryId())
                .categoryName(resultItem.getCategoryName())
                .status(resultItem.getStatus().getKoreanCode())
                .isDirectTrade(resultItem.getIsDirectTrade())

                .imageIds(itemCreateRequestDto.getImageIds())
                .imageUrls(itemImageUrls)

                .location(resultItem.getLocation())
                .startPrice(resultAuction.getStartPrice())
                .instantSalePrice(resultAuction.getInstantSalePrice())
                .bidUnit(resultAuction.getBidUnit())
                .seller(UserResponseDto.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .profileImageUrl(profileImageUrl)
                        .build())
                .createdAt(resultAuction.getCreatedAt())
                .auctionStartTime(resultAuction.getAuctionStartTime())
                .auctionEndTime(resultAuction.getAuctionEndTime())
                .build();
    }

    @Transactional
    public ItemResponseDto findItemWithAuctionByItemId(Long itemId, Long userId) {

        log.info("findItemWithAuctionByItemId() 현재 스레드: {}", Thread.currentThread().getName());

        Item findItem = itemRepository.findWithAuctionAndUserById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("조회된 상품이 없습니다. (입력: " + itemId + ")"));
        Auction findAuction = findItem.getAuction();

        // TODO 좋아요 여부 개발
        boolean isLiked = itemLikeService.checkItemLikeStatus(userId, itemId);

        // TODO 보증급 납부여부 개발
        boolean isDepositPaid = false;

        User seller = userService.findById(findItem.getUser().getId());

        List<File> files = fileService.findFilesByItemIdOrUserId(findItem.getId(), userId);
        String profileImageUrl = null;
        List<String> itemImageUrls = new ArrayList<>();
        for (File file : files) {
            if (file.getItem() == null && profileImageUrl == null)
                profileImageUrl = file.getFileUrl();
            else
                itemImageUrls.add(file.getFileUrl());
        }

        // 조회수는 별도의 스레드에서 증가만 시킨다.
        itemAsyncService.increaseViewCount(itemId);

        return ItemResponseDto.builder()
                .itemId(findItem.getId())
                .auctionId(findAuction.getId())
                .title(findItem.getTitle())
                .description(findItem.getDescription())
                .categoryId(findItem.getCategoryId())
                .categoryName(categoryService.findCategoryNameById(findItem.getCategoryId()))
                .imageIds(List.of("1", "2", "3"))
                .imageUrls(itemImageUrls)
                .status(findItem.getStatus().getKoreanCode())
                .isDirectTrade(findItem.getIsDirectTrade())
                .location(findItem.getLocation())
                .viewCount(findItem.getViewCount() + 1)
                .likeCount(findItem.getLikeCount())
                .chatCount(findItem.getChatCount())
                .isLiked(isLiked)
                .isDepositPaid(isDepositPaid)
                .seller(UserResponseDto.builder()
                        .userId(seller.getId())
                        .nickname(seller.getNickname())
                        .profileImageUrl(profileImageUrl)
                        .build())
                .auction(AuctionDto.builder()
                        .startPrice(findAuction.getStartPrice())
                        .currentBid(findAuction.getCurrentBid())
                        .instantSalePrice(findAuction.getInstantSalePrice())
                        .instantSaleEndTime(findAuction.getInstantSaleEndTime())
                        .bidUnit(findAuction.getBidUnit())
                        .bidCount(findAuction.getBidCount())
                        .auctionStartTime(findAuction.getAuctionStartTime())
                        .auctionEndTime(findAuction.getAuctionEndTime())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public CursorResult<ItemSummaryDto> getItems(String title, String sortColumn, String sortDirection, String cursor, int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);

        // ItemSummaryDto 조회
        CursorResult<ItemSummaryDto> search = itemRepository.search(title, sortColumn, sortDirection, cursor, pageable);
        List<ItemSummaryDto> ItemSummaryDtoList = search.getContent();

        // 상품의 대표 이미지 조회
        List<Long> itemIds = ItemSummaryDtoList.stream().map(ItemSummaryDto::getItemId).toList();
        List<File> representativeImagesRaw = fileService.findFileRepresentativeFilesByItemIds(itemIds);

        // 대표 이미지 Map 으로 변환
        Map<Long, String> imageMap = representativeImagesRaw.stream()
                .collect(Collectors.toMap(
                        file -> file.getItem().getId(),
                        File::getFileUrl
                ));

        // ItemSummaryDto에 대표 이미지 Merge
        ItemSummaryDtoList.forEach(ItemSummaryDto ->
                ItemSummaryDto.setThumbnailUrl(imageMap.get(ItemSummaryDto.getItemId()))
        );

        return search;
    }

    @Transactional(readOnly = true)
    public Item findWithAuctionByAuctionId(Long auctionId) {
        return itemRepository.findItemWithAuctionAndLockByAuctionId(auctionId).orElseThrow(ItemNotFoundException::new);
    }
}
