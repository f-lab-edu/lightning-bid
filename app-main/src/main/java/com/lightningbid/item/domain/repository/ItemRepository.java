package com.lightningbid.item.domain.repository;

import com.lightningbid.item.domain.model.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    @Modifying
    @Query("UPDATE Item i SET i.viewCount = i.viewCount + 1 WHERE i.id = :id")
    void increaseViewCount(Long id);

    @Query("SELECT i FROM Item i JOIN FETCH i.auction a JOIN FETCH i.user u WHERE i.id = :itemId")
    Optional<Item> findWithAuctionAndUserById(Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i JOIN FETCH i.auction a WHERE a.id = :auctionId")
    Optional<Item> findItemWithAuctionAndLockByAuctionId(Long auctionId);
}
