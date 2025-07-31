package com.lightningbid.item.item.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepository {

    @Modifying
    public void increaseViewCount(Long id) {
//         SET viewCount = viewCount + 1
    }

}
