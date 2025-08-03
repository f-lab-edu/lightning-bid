package com.lightningbid.auction.service;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.auction.domain.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    private final BidUnitService bidUnitService;

    @Transactional
    public Auction createAuction(Auction auction) {

        // 입찰 단위 설정
        BigDecimal bidUnit = bidUnitService.getBidUnit(auction.getStartPrice());
        auction.applyBidUnit(bidUnit);

        validateAuction(auction);

        return auctionRepository.save(auction);
    }

    private void validateAuction(Auction auction) {

        LocalDateTime auctionStartTime = auction.getAuctionStartTime();
        LocalDateTime auctionEndTime = auction.getAuctionEndTime();

        if (auctionEndTime.isAfter(auctionStartTime.plusDays(14)))
            throw new IllegalArgumentException("경매 종료일은 14일 이내여야 합니다. (입력: " + auctionEndTime + ")");

        if (auctionEndTime.isBefore(auctionStartTime.plusDays(1)))
            throw new IllegalArgumentException("경매 기간은 최소 24시간 이상 이어야 합니다. (입력: " + auctionEndTime + ")");

        BigDecimal startPrice = auction.getStartPrice(); // 경매 시작 가격
        BigDecimal instantSalePrice = auction.getInstantSalePrice(); // 즉시 판매 가격

        if (instantSalePrice != null) {
            if (startPrice.compareTo(instantSalePrice) > 0)
                throw new IllegalArgumentException("즉시 판매 가격은 경매 시작가보다 높아야 합니다.");

            if (instantSalePrice.compareTo(startPrice) != 0 &&
                    instantSalePrice.subtract(startPrice).compareTo(auction.getBidUnit()) < 0)
                throw new IllegalArgumentException("즉시 판매 가격은 입찰 단위보다 높아야 합니다. 입찰 단위: " + auction.getBidUnit());
        }
    }
}
