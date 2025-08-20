package com.lightningbid.auction.service;

import com.lightningbid.auction.domain.exception.*;
import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.auction.domain.model.Bid;
import com.lightningbid.auction.domain.repository.BidRepository;
import com.lightningbid.auction.web.dto.request.BidCreateRequestDto;
import com.lightningbid.auction.web.dto.response.BidCreateResponseDto;
import com.lightningbid.item.domain.enums.ItemStatus;
import com.lightningbid.item.domain.model.Item;
import com.lightningbid.item.exception.ItemNotActiveException;
import com.lightningbid.item.service.ItemService;
import com.lightningbid.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class BidService {

    private final BidRepository bidRepository;

    private final UserRepository userRepository;

    private final ItemService itemService;

    @Transactional
    public BidCreateResponseDto addBid(BidCreateRequestDto requestDto, Long auctionId, Long userId) {

        Item itemWithAuction = itemService.findWithAuctionByAuctionId(auctionId);
        if (ItemStatus.ACTIVE != itemWithAuction.getStatus())
            throw new ItemNotActiveException();

        // TODO bid 테이블이 아닌 보증금 납부 테이블로 조회
        // 보증금 납부한 이력이 있는지 확인
        if (!bidRepository.existsByAuctionIdAndUserId(auctionId, userId))
            throw new DepositRequiredException();

        Auction findAuction = itemWithAuction.getAuction();

        // 입찰 요청 가격
        BigDecimal bidRequestPrice = requestDto.getPrice();
        // 즉시 판매 가격
        BigDecimal instantSalePrice = findAuction.getInstantSalePrice();

        // 입찰가가 즉시판매 가격보다 높을경우 처리
        if (instantSalePrice != null && instantSalePrice.compareTo(bidRequestPrice) < 0)
            throw new OverInstantSalePriceException();

        // 즉시 구매 입찰 여부 확인
        boolean isInstantSaleBid = instantSalePrice != null && instantSalePrice.compareTo(bidRequestPrice) == 0;

        LocalDateTime instantSaleEndTime = null;

        if (isInstantSaleBid) {
            instantSaleEndTime = LocalDateTime.now();
            itemWithAuction.updateStatus(ItemStatus.PENDING);

        } else {
            // 현재 최고 입찰 금액
            BigDecimal currentBestBidAmount = findAuction.getCurrentBid();

            // 최고 입찰 금액 이하로 입력
            if (bidRequestPrice.compareTo(currentBestBidAmount) <= 0)
                throw new BidAmountTooLowException();

            // 현재 입찰 단위
            BigDecimal requestBidUnit = bidRequestPrice.subtract(currentBestBidAmount);

            // 최소 입찰 단위 검증
            if (requestBidUnit.compareTo(findAuction.getBidUnit()) < 0) {

                // bidUnit > (instantSalePrice - currentBestBidAmount)
                if (instantSalePrice != null &&
                        findAuction.getBidUnit().compareTo(instantSalePrice.subtract(currentBestBidAmount)) > 0)
                    throw new BidUnitTooSmallException("즉시 판매 금액으로만 입찰이 가능합니다. (즉시 판매 금액:" + instantSalePrice + ")");

                throw new BidUnitTooSmallException("최소 입찰 단위(" + findAuction.getBidUnit().intValue() + ")보다 큰 금액으로 입찰 선청이 가능합니다.");
            }
        }

        Bid savedBid = bidRepository.save(Bid.builder()
                .amount(bidRequestPrice)
                .auction(findAuction)
                .user(userRepository.getReferenceById(userId))
                .build());

        findAuction.updateCurrentBid(bidRequestPrice, instantSaleEndTime);

        return BidCreateResponseDto.builder()
                .bidId(savedBid.getId())
                .auctionId(savedBid.getAuction().getId())
                .currentBid(savedBid.getAmount())
                .itemStatus(itemWithAuction.getStatus().getCode())
                .build();
    }
}
