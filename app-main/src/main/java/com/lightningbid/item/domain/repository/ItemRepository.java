package com.lightningbid.item.domain.repository;

import com.lightningbid.item.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Query("update Item i set i.viewCount = i.viewCount + 1 where i.id = :id")
    void increaseViewCount(Long id);

    @Query("select i, a from Item i join fetch i.auction a where i.id = :id")
    Optional<Item> findWithAuctionById(Long id);
}
