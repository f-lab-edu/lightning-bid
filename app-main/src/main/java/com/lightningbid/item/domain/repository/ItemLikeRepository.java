package com.lightningbid.item.domain.repository;

import com.lightningbid.item.domain.model.ItemLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemLikeRepository extends JpaRepository<ItemLike, Long>, ItemLikeRepositoryCustom {

    @Query("SELECT il FROM ItemLike il WHERE il.user.id = :userId AND il.item.id = :itemId")
    Optional<ItemLike> findByUserIdAndItemId(Long userId, Long itemId);
}
