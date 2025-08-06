package com.lightningbid.item.domain.model;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.common.entity.BaseEntity;
import com.lightningbid.item.domain.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private Long userId;

    private String title;

    private String description;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Column(nullable = false)
    private Boolean isDirectTrade;

    private String location;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int viewCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int likeCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int chatCount;

    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private Auction auction;
}
