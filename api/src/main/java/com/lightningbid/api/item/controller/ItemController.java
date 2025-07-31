package com.lightningbid.api.item.controller;

import com.lightningbid.api.item.dto.request.ItemCreateRequestDto;
import com.lightningbid.api.item.dto.request.ItemPatchRequestDto;
import com.lightningbid.api.item.dto.request.ItemsRequestDto;
import com.lightningbid.api.item.dto.response.*;
import com.lightningbid.api.user.dto.response.UserDto;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.auction.domain.Auction;
import com.lightningbid.item.item.domain.Item;
import com.lightningbid.item.item.enums.ItemStatus;
import com.lightningbid.auction.service.AuctionService;
import com.lightningbid.item.category.service.CategoryService;
import com.lightningbid.item.itemlike.service.ItemLikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final CategoryService categoryService;

    private final AuctionService auctionService;

    private final ItemLikeService itemLikeService;

    @PostMapping
    public ResponseEntity<CommonResponseDto<ItemCreateResponseDto>> createItem(@RequestBody @Valid ItemCreateRequestDto requestDto) {

        // 이미지 처리 해야됨

        Item item = Item.builder()
                .title(requestDto.getTitle())
                .userId(1L)
                .description(requestDto.getDescription())
                .categoryId(requestDto.getCategoryId())
                .categoryName(categoryService.findCategoryNameById(requestDto.getCategoryId()))
                .isDirectTrade(requestDto.getIsDirectTrade())
                .location(requestDto.getLocation())
                .build();

        Auction resultAuction = auctionService.createAuction(Auction.builder()
                        .startPrice(requestDto.getStartPrice())
                        .currentBid(requestDto.getStartPrice())
                        .instantSalePrice(requestDto.getInstantSalePrice())
                        .item(item)
                        .build(),
                requestDto.getAuctionDuration()
        );

        /*----------------------------------------*/
        // TODO: repository 개발 후 수정
//        Item resultItem = resultAuction.getItem();
        item.setViewCount(0);
        item.setId(1L);
        item.setStatus(ItemStatus.ACTIVE);
        item.setCreatedAt(LocalDateTime.now());
        Item resultItem = item;
        /*----------------------------------------*/

        ItemCreateResponseDto productResponseDto = ItemCreateResponseDto.builder()
                .itemId(resultItem.getId())
                .title(resultItem.getTitle())
                .description(resultItem.getDescription())
                .categoryId(resultItem.getCategoryId())
                .categoryName(resultItem.getCategoryName())
                .status(resultItem.getStatus().getCode())
                .isDirectTrade(resultItem.getIsDirectTrade())

                .imageIds(requestDto.getImageIds())
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
                .createdAt(resultItem.getCreatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponseDto.success(HttpStatus.CREATED.value(), "상품이 성공적으로 등록되었습니다.", productResponseDto)
        );
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto<ItemsResponseDto>> getItems(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @ModelAttribute ItemsRequestDto requestDto) {

        List<ItemSummaryDto> itemSummaryDtoList = new ArrayList<>();
        itemSummaryDtoList.add(ItemSummaryDto.builder()
                .itemId(12340L)
                .title("M2 맥북 에어 미드나이트")
                .thumbnailUrl("https://example.com/thumbnails/macbook_air.jpg")
                .location("서울특별시 강남구")
                .seller(UserDto.builder()
                        .userId(801L)
                        .nickname("강남판매자")
                        .profileImageUrl("https://...")
                        .build())
                .price(1150000)
                .currentBid(null)
                .status(ItemStatus.ACTIVE.getCode())
                .createdAt(LocalDateTime.of(2025, 7, 17, 13, 50, 0))
                .viewCount(150)
                .likeCount(18)
                .bidCount(5)
                .build());

        itemSummaryDtoList.add(ItemSummaryDto.builder()
                .itemId(12321L)
                .title("LG 스탠바이미 TV 거의 새것")
                .thumbnailUrl("https://...")
                .location("서울특별시 서초구")
                .seller(UserDto.builder()
                        .userId(801L)
                        .nickname("서초판매자")
                        .profileImageUrl("https://...")
                        .build())
                .price(700000)
                .currentBid(750000) // 경매가 진행 중인 경우
                .status(ItemStatus.ACTIVE.getCode())
                .createdAt(LocalDateTime.of(2025, 7, 17, 13, 30, 0))
                .viewCount(100)
                .likeCount(10)
                .bidCount(3)
                .build());

        ItemSummaryDto lastItem = itemSummaryDtoList.getLast();
        CursorDto nextCursor = CursorDto.builder()
                .createdAt(lastItem.getCreatedAt()) // 마지막 상품의 생성 시간
                .id(lastItem.getItemId()) // 마지막 상품의 ID
                .build();

        ItemsResponseDto itemList = ItemsResponseDto.builder()
                .content(itemSummaryDtoList)
                .pageInfo(PageInfoDto.builder()
                        .size(pageable.getPageSize())
                        .hasNext(true)
                        .nextCursor(nextCursor)
                        .build())
                .build();

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "상품 목록 조회가 완료되었습니다.", itemList));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<CommonResponseDto<ItemResponseDto>> getItemDetail(@PathVariable Long itemId) {

        Auction auction = auctionService.findAuctionByItemId(itemId);
        Item item = auction.getItem();

        boolean isLiked = itemLikeService.checkUserLikeStatus(1L, itemId);
        String categoryName = categoryService.findCategoryNameById(1L);

        // TODO 보증금 납부 여부 확인 로직 추가
        boolean isDepositPaid = false;

        ItemResponseDto itemDetail = ItemResponseDto.builder()
                .itemId(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .categoryId(item.getCategoryId())
                .categoryName(categoryName)
                .imageIds(List.of("1", "2", "3"))
                .imageUrls(List.of(
                        "https://...",
                        "https://...",
                        "https://..."
                ))
                .status(item.getStatus().getCode())
                .isDirectTrade(item.getIsDirectTrade())
                .location(item.getLocation())
                .viewCount(item.getViewCount() + 1)
                .likeCount(item.getLikeCount())
                .chatCount(item.getChatCount())
                .isLiked(isLiked)
                .isDepositPaid(isDepositPaid)
                .seller(UserDto.builder()
                        .userId(789L)
                        .nickname("판매자_닉네임")
                        .profileImageUrl("https://...")
                        .build())
                .auction(AuctionDto.builder()
                        .startPrice(auction.getStartPrice())
                        .currentBid(auction.getCurrentBid())
                        .bidUnit(auction.getBidUnit())
                        .bidCount(auction.getBidCount())
                        .auctionStartTime(auction.getAuctionStartTime())
                        .auctionEndTime(auction.getAuctionEndTime())
                        .build())
                .build();

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "상품 상세 정보 조회가 완료되었습니다.", itemDetail));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<CommonResponseDto<ItemResponseDto>> patchItem(@PathVariable Long itemId, @RequestBody ItemPatchRequestDto requestDto) {

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .itemId(itemId)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .categoryId(requestDto.getCategoryId())
                .categoryName("디지털기기")
                .imageIds(List.of("1", "2", "3"))
                .imageUrls(List.of(
                        "https://...",
                        "https://...",
                        "https://..."
                ))
                .status(ItemStatus.ACTIVE.getCode())
                .isDirectTrade(requestDto.getIsDirectTrade())
                .location(requestDto.getLocation())
                .viewCount(152)
                .likeCount(12)
                .chatCount(3)
                .isLiked(true)
                .isDepositPaid(false)
                .seller(UserDto.builder()
                        .userId(789L)
                        .nickname("판매자_닉네임")
                        .profileImageUrl("https://...")
                        .build())
                .auction(AuctionDto.builder()
                        .startPrice(requestDto.getStartPrice())
                        .currentBid(BigDecimal.valueOf(35000))
                        .bidUnit(BigDecimal.valueOf(1000))
                        .bidCount(0)
//                        .auctionStartTime(requestDto.getAuctionEndTime())
                        .auctionEndTime(requestDto.getAuctionEndTime())
                        .build())
                .build();

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "상품 정보가 성공적으로 수정되었습니다.", itemResponseDto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {

        return ResponseEntity.noContent().build();
    }
}
