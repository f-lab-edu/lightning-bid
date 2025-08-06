package com.lightningbid.item.web.controller;

import com.lightningbid.item.service.ItemService;
import com.lightningbid.item.web.dto.request.ItemCreateRequestDto;
import com.lightningbid.item.web.dto.request.ItemPatchRequestDto;
import com.lightningbid.item.web.dto.request.ItemsRequestDto;
import com.lightningbid.item.web.dto.response.*;
import com.lightningbid.user.web.dto.response.UserResponseDto;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.item.domain.enums.ItemStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<CommonResponseDto<ItemCreateResponseDto>> createItem(@RequestBody @Valid ItemCreateRequestDto requestDto) {

        Duration auctionDuration;
        try {
            auctionDuration = Duration.parse(requestDto.getAuctionDuration());
        } catch (DateTimeParseException e) {
            // ISO 8601 형식이 아니면 예외 발생
            throw new IllegalArgumentException("유효하지 않은 기간 형식입니다. (입력: " + requestDto.getAuctionDuration() + ")");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponseDto.success(HttpStatus.CREATED.value(), "상품이 성공적으로 등록되었습니다.", itemService.createItemWithAuction(requestDto, auctionDuration))
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
                .seller(UserResponseDto.builder()
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
                .seller(UserResponseDto.builder()
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

        return ResponseEntity.ok(CommonResponseDto.success(
                HttpStatus.OK.value(),
                "상품 목록 조회가 완료되었습니다.",
                itemList));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<CommonResponseDto<ItemResponseDto>> getItemDetail(@PathVariable Long itemId) {

        return ResponseEntity.ok(CommonResponseDto.success(
                HttpStatus.OK.value(),
                "상품 상세 정보 조회가 완료되었습니다.",
                itemService.findItemWithAuctionByItemId(itemId)));
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
                .seller(UserResponseDto.builder()
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
