package com.lightningbid.auction.domain.repository;

import com.lightningbid.auction.domain.model.BidUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BidUnitRepository extends JpaRepository<BidUnit, Long> {

    /**
     * price 보다 크거나 같은 금액 중 가장 작은 row를 가져온다.
     * @param price 현재 아이템 가격
     * @return 해당하는 BidUnit 규칙
     */
    Optional<BidUnit> findFirstByPriceGreaterThanEqualOrderByPriceAsc(BigDecimal price);
}
