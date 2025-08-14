package com.lightningbid.item.domain.model;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.common.entity.BaseEntity;
import com.lightningbid.file.domain.model.File;
import com.lightningbid.item.domain.enums.ItemStatus;
import com.lightningbid.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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

    @OneToOne(mappedBy = "item")
    private Auction auction;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<File> files = new ArrayList<>();

    public void updateStatus(ItemStatus status) {
        this.status = status;
    }
}
