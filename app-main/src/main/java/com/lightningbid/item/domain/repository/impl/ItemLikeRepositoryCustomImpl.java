package com.lightningbid.item.domain.repository.impl;

import com.lightningbid.item.domain.repository.ItemLikeRepositoryCustom;
import com.lightningbid.item.web.dto.request.ItemLikeEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemLikeRepositoryCustomImpl implements ItemLikeRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void bulkUpsertAndToggle(List<ItemLikeEventDto> likeEvents) {
        String sql =
                "INSERT INTO app.item_like (" +
                        "user_id, " +
                        "item_id, " +
                        "is_liked, " +
                        "delete_yn, " +
                        "created_at, " +
                        "created_by, " +
                        "last_modified_at, " +
                        "last_modified_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (user_id, item_id) DO UPDATE SET " +
                        "  is_liked = NOT app.item_like.is_liked, " +
                        "  last_modified_at = EXCLUDED.last_modified_at, " +
                        "  last_modified_by = EXCLUDED.last_modified_by";

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.batchUpdate(sql,
                likeEvents,
                1000,
                (PreparedStatement ps, ItemLikeEventDto event) -> {

                    Long userId = event.getUserId();

                    ps.setLong(1, userId);
                    ps.setLong(2, event.getItemId());
                    ps.setBoolean(3, true);
                    ps.setBoolean(4, false);
                    ps.setTimestamp(5, Timestamp.valueOf(now));
                    ps.setLong(6, userId);
                    ps.setTimestamp(7, Timestamp.valueOf(now));
                    ps.setLong(8, userId);
                });
    }
}
