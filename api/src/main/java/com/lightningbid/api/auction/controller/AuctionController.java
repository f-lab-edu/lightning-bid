package com.lightningbid.api.auction.controller;

import com.lightningbid.api.auction.dto.request.BidCreateRequestDto;
import com.lightningbid.api.auction.dto.response.*;
import com.lightningbid.api.user.dto.response.UserDto;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.enums.ConfirmationStatus;
import com.lightningbid.enums.ItemStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class AuctionController {

    @PostMapping("/{itemId}/bids")
    public ResponseEntity<CommonResponseDto<BidCreateResponseDto>> createBid(
            @RequestBody @Valid BidCreateRequestDto requestDto,
            @PathVariable long itemId) {

        BidCreateResponseDto responseDto = BidCreateResponseDto.builder()
                .bidId(1L)
                .itemId(itemId)
                .currentBid(requestDto.getPrice())
                .bidCount(1)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponseDto.success(HttpStatus.CREATED.value(), "입찰이 성공적으로 등록되었습니다.", responseDto)
        );
    }

    @GetMapping("/{itemId}/bids")
    public ResponseEntity<CommonResponseDto<BidsResponse>> getBidsByItemId(@PathVariable long itemId) {

        List<BidDetailDto> bidList = new ArrayList<>();
        bidList.add(
                BidDetailDto.builder()
                        .bidId(500L)
                        .bidder(UserDto.builder()
                                .userId(800L)
                                .nickname("입찰참가자A")
                                .profileImageUrl("https://...")
                                .build())
                        .price(35000)
                        .bidAt(LocalDateTime.of(2025, 7, 17, 13, 50, 0))
                        .build()
        );

        bidList.add(
                BidDetailDto.builder()
                        .bidId(501L)
                        .bidder(UserDto.builder()
                                .userId(801L)
                                .nickname("입찰참가자B")
                                .profileImageUrl("https://...")
                                .build())
                        .price(38000)
                        .bidAt(LocalDateTime.of(2025, 7, 19, 14, 12, 0))
                        .build()
        );

        BidsResponse responseDto = BidsResponse.builder().bids(bidList).build();

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "상품의 전체 입찰 목록 조회가 완료되었습니다.", responseDto));
    }

    @DeleteMapping("/{itemId}/bids/{bidId}")
    public ResponseEntity<CommonResponseDto<Void>> cancelBid(
            @PathVariable long itemId,
            @PathVariable long bidId) {

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "입찰 취소 처리되었습니다."));
    }

    @PostMapping("/{itemId}/deals/confirm")
    public ResponseEntity<CommonResponseDto<BidConfirmResponseDto>> confirmDeal(@PathVariable long itemId) {

        BidConfirmResponseDto responseDto =
                BidConfirmResponseDto.builder()
                        .itemId(itemId)
                        .status(ItemStatus.PENDING.getCode())
                        .sellerConfirmationStatus(ConfirmationStatus.PENDING.getCode())
                        .buyerConfirmationStatus(ConfirmationStatus.CONFIRMED.getCode())
                        .build();

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "거래 확정 처리되었습니다.", responseDto));
    }

    @PatchMapping("/{itemId}/deals/cancel")
    public ResponseEntity<CommonResponseDto<AuctionStatusResponseDto>> reportDealCancellation(@PathVariable long itemId) {

        AuctionStatusResponseDto responseDto = AuctionStatusResponseDto.builder()
                .itemId(itemId)
                .status(ItemStatus.FAILED.getCode())
                .build();

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "거래 불발 처리가 완료되었습니다.", responseDto));
    }

    @PatchMapping("/{itemId}/cancel")
    public ResponseEntity<CommonResponseDto<AuctionStatusResponseDto>> stopAuction(@PathVariable long itemId) {

        AuctionStatusResponseDto responseDto = AuctionStatusResponseDto.builder()
                .itemId(itemId)
                .status(ItemStatus.CANCELED.getCode())
                .build();

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "경매가 취소되었습니다.", responseDto));
    }
}
