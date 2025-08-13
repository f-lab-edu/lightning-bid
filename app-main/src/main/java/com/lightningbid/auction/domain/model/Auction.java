package com.lightningbid.auction.domain.model;

import com.lightningbid.common.entity.BaseEntity;
import com.lightningbid.item.domain.model.Item;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Auction extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auciton_id")
    private long id;

    @Column(nullable = false)
    private BigDecimal startPrice;

    @Column(nullable = false)
    private BigDecimal currentBid;

    private BigDecimal instantSalePrice;

    @Column(nullable = false)
    private BigDecimal bidUnit;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int bidCount;

    @Column(nullable = false)
    private LocalDateTime auctionStartTime;

    @Column(nullable = false)
    private LocalDateTime auctionEndTime;

    private LocalDateTime instantSaleEndTime;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private Item item;

    public void applyBidUnit(BigDecimal bidUnit) {
        this.bidUnit = bidUnit;
    }

    public void updateCurrentBid(BigDecimal currentBid, LocalDateTime instantSaleEndTime) {
        this.currentBid = currentBid;
        this.bidCount += 1;
        if (instantSaleEndTime != null) {
            this.instantSaleEndTime = instantSaleEndTime;
        }
    }
}
