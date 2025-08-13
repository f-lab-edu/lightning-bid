package com.lightningbid.auction.service;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.auction.domain.repository.AuctionRepository;
import com.lightningbid.auction.exception.AuctionValidationException;
import com.lightningbid.common.enums.ErrorCode;
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

        BigDecimal bidUnit = auction.getBidUnit() == null ?
                bidUnitService.getBidUnitByPrice(auction.getStartPrice()) : auction.getBidUnit();

        // 입찰 단위 설정
        auction.applyBidUnit(bidUnit);

        validateAuction(auction);

        return auctionRepository.save(auction);
    }

    private void validateAuction(Auction auction) {

        LocalDateTime auctionStartTime = auction.getAuctionStartTime();
        LocalDateTime auctionEndTime = auction.getAuctionEndTime();

        if (auctionEndTime.isAfter(auctionStartTime.plusDays(14)))
            throw new AuctionValidationException("경매 종료일은 14일 이내여야 합니다. (입력: " + auctionEndTime + ")", ErrorCode.AUCTION_PERIOD_TOO_LONG);

        if (auctionEndTime.isBefore(auctionStartTime.plusDays(1)))
            throw new AuctionValidationException("경매 기간은 최소 24시간 이상 이어야 합니다. (입력: " + auctionEndTime + ")", ErrorCode.AUCTION_PERIOD_TOO_SHORT);

        BigDecimal startPrice = auction.getStartPrice(); // 경매 시작 가격
        BigDecimal instantSalePrice = auction.getInstantSalePrice(); // 즉시 판매 가격

        if (instantSalePrice != null) {

            if (instantSalePrice.compareTo(startPrice) < 0) {
                throw new AuctionValidationException("즉시 판매 가격은 경매 시작가보다 높아야 합니다.", ErrorCode.INSTANT_PRICE_BELOW_START);
            }

            BigDecimal bidUnit = auction.getBidUnit();

            if (instantSalePrice.compareTo(startPrice) != 0 &&
                    instantSalePrice.subtract(startPrice).compareTo(bidUnit) < 0)
                throw new AuctionValidationException("즉시 판매 가격은 (경매 시작 가격 + 입찰 단위) 보다 높은 금액 이어야 합니다. 입찰 단위: " + bidUnit, ErrorCode.INSTANT_PRICE_STEP_TOO_SMALL);

            if (instantSalePrice.compareTo(bidUnit) < 0)
                throw new AuctionValidationException("입찰 단위는 즉시 판매 가격보다 낮아야 합니다.", ErrorCode.BID_UNIT_OVER_INSTANT_SALE);

            // (즉시 판매가격 - 판매가격) % 입찰단위 != 0
            if (instantSalePrice.subtract(startPrice).remainder(bidUnit).compareTo(BigDecimal.ZERO) != 0)
                throw new AuctionValidationException("즉시 판매 가격이 입찰 단위와 맞지 않습니다. 즉시 판매 가격은 경매 시작 금액에 입찰 단위의 배수로 입력해 주세요.", ErrorCode.BID_UNIT_NOT_INSTANT_MULTIPLE);
        }
    }
}
