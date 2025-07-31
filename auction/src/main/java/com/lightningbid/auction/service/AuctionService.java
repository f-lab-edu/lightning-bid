package com.lightningbid.auction.service;

import com.lightningbid.common.exception.ResourceNotFoundException;
import com.lightningbid.auction.domain.Auction;
import com.lightningbid.item.item.service.ItemService;
import com.lightningbid.auction.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionService {

    private final ItemService itemService;

    private final AuctionRepository auctionRepository;

    public Auction createAuction(Auction auction, String auctionDuration) {

        // 클라이언트에서 받은 값 체크
        validateAuction(auction, auctionDuration);

        // 입찰 단위 설정
        int bidUnit = auctionRepository.findBidUnit(auction.getStartPrice());
        auction.setBidUnit(BigDecimal.valueOf(bidUnit));

        return auctionRepository.save(auction);
    }

    private void validateAuction(Auction auction, String auctionDuration) {

        Duration duration;
        try {
            duration = Duration.parse(auctionDuration);
        } catch (DateTimeParseException e) {
            // ISO 8601 형식이 아니면 예외 발생
            throw new IllegalArgumentException("유효하지 않은 기간 형식입니다. (입력: " + auctionDuration + ")");
        }

        LocalDateTime auctionStartTime = LocalDateTime.now();
        LocalDateTime auctionEndTime = auctionStartTime.plus(duration);
        auction.setAuctionStartTime(auctionStartTime);
        auction.setAuctionEndTime(auctionStartTime.plus(duration));

        if (auctionEndTime.isAfter(auctionStartTime.plusDays(14)))
            throw new IllegalArgumentException("경매 종료일은 14일 이내여야 합니다. (입력: " + auctionEndTime + ")");

        if (auctionEndTime.isBefore(auctionStartTime.plusDays(1)))
            throw new IllegalArgumentException("경매 기간은 최소 24시간 이상 이어야 합니다. (입력: " + auctionEndTime + ")");

        BigDecimal startPrice = auction.getStartPrice(); // 경매 시작 가격
        BigDecimal instantSalePrice = auction.getInstantSalePrice(); // 즉시 판매 가격

        if (instantSalePrice != null && startPrice.compareTo(instantSalePrice) > 0)
            throw new IllegalArgumentException("즉시 판매 가격은 경매 시작가보다 높아야 합니다.");
    }

    @Transactional(readOnly = true)
    public Auction findAuctionByItemId(Long itemId) {

        Auction auction = auctionRepository.findWithItemByItemId(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("조회된 상품이 없습니다."));

        // 조회수는 증가만 시킨다.
        itemService.increaseViewCount(itemId);

        return auction;
    }
}
