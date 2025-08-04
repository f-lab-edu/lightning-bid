package com.lightningbid.auction.domain.repository;

import com.lightningbid.auction.domain.model.Auction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Repository
public class AuctionRepository {

    public Auction save(Auction auction) {
        return auction;
    }

    public int findBidUnit(BigDecimal startPrice) {
        int bidUnit = startPrice.divide(BigDecimal.valueOf(10), 0, RoundingMode.DOWN).intValue();
        return bidUnit;
    }

    public Optional<Auction> findWithItemByItemId(Long itemId) {
        Auction auction = new Auction();
        return Optional.of(auction);
    }

}
