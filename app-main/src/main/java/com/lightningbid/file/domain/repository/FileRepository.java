package com.lightningbid.file.domain.repository;

import com.lightningbid.file.domain.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByUuid(String uuid);

    @Modifying(clearAutomatically = true) // 영속성 컨테이너 1차 캐시 비우는 옵션
    @Query("UPDATE " +
                "File f " +
            "SET " +
                "f.deleteYn = true, " +
                "f.lastModifiedAt = CURRENT_TIMESTAMP, " +
                "f.lastModifiedBy = :userId " +
            "WHERE " +
                "f.user.id = :userId " +
                "AND f.id != :fileIdToKeep")
    void softDeleteByExcludingIdAndUserId(Long fileId, Long userId);
}
