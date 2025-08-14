package com.lightningbid.file.domain.repository;

import com.lightningbid.file.domain.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByUuid(String uuid);

    List<File> findByItemIdOrUserId(Long itemId, Long userId);

    @Query("""
            SELECT f
            FROM File f
            WHERE f.id IN (
                SELECT MIN(sf.id)
                FROM File sf
                WHERE sf.item.id IN :itemIds
                GROUP BY sf.item.id
            )
            """)
    List<File> findRepresentativeFilesByItemIds(List<Long> itemIds);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE
               File f
            SET
               f.item.id = :itemId,
               f.lastModifiedAt = CURRENT_TIMESTAMP,
               f.lastModifiedBy = :userId
            WHERE
               f.uuid IN :uuids
            """)
    void updateItemIdByUuidIn(Long itemId, Long userId, List<String> uuids);


}
