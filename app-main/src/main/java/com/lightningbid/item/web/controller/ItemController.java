package com.lightningbid.item.web.controller;

import com.lightningbid.auction.exception.AuctionValidationException;
import com.lightningbid.auth.dto.CustomOAuth2User;
import com.lightningbid.common.dto.CursorResult;
import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.item.service.ItemService;
import com.lightningbid.item.web.dto.request.ItemCreateRequestDto;
import com.lightningbid.item.web.dto.request.ItemPatchRequestDto;
import com.lightningbid.item.web.dto.response.*;
import com.lightningbid.user.web.dto.response.UserResponseDto;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.item.domain.enums.ItemStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<CommonResponseDto<ItemCreateResponseDto>> createItem(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody @Valid ItemCreateRequestDto requestDto) {

        Duration auctionDuration;
        try {
            auctionDuration = Duration.parse(requestDto.getAuctionDuration());
        } catch (DateTimeParseException e) {
            // ISO 8601 형식이 아니면 예외 발생
            throw new AuctionValidationException("유효하지 않은 기간 형식입니다. (입력: " + requestDto.getAuctionDuration() + ")", ErrorCode.AUCTION_DURATION_FORMAT_INVALID);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponseDto.success(HttpStatus.CREATED.value(), "상품이 성공적으로 등록되었습니다.", itemService.createItemWithAuction(requestDto, auctionDuration, user))
        );
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto<CursorResult<ItemSummaryDto>>> getItems(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "createdAt") String sortColumn,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int pageSize
    ) {

        return ResponseEntity.ok(CommonResponseDto.success(
                HttpStatus.OK.value(),
                "상품 목록 조회가 완료되었습니다.",
                itemService.getItems(title, sortColumn, sortDirection, cursor, pageSize))
        );
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<CommonResponseDto<ItemResponseDto>> getItemDetail(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long itemId) {

        return ResponseEntity.ok(CommonResponseDto.success(
                HttpStatus.OK.value(),
                "상품 상세 정보 조회가 완료되었습니다.",
                itemService.findItemWithAuctionByItemId(itemId, user.getId())));
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
