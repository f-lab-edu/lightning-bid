package com.lightningbid.auction.service;

import com.lightningbid.auction.domain.model.BidUnit;
import com.lightningbid.auction.domain.repository.BidUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BidUnitService {

    private final BidUnitRepository bidUnitRepository;

    private static final BigDecimal DEFAULT_BID_UNIT = BigDecimal.valueOf(10_000);

    public BigDecimal getBidUnit(BigDecimal price) {

        return bidUnitRepository.findFirstByPriceGreaterThanEqualOrderByPriceAsc(price)
                .map(BidUnit::getUnit)
                .orElse(DEFAULT_BID_UNIT);
    }

}
