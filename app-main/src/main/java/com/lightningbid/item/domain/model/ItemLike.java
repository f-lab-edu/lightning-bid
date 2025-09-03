package com.lightningbid.item.domain.model;

import com.lightningbid.common.entity.BaseEntity;
import com.lightningbid.user.domain.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "item_id"})
})
public class ItemLike extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private Boolean isLiked;

    public void toggleLikeStatus() {
        isLiked = !isLiked;
    }
 }
