package com.capstone.arfly.common.repository;

import com.capstone.arfly.common.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByDeletedTrue();

    @Query("""
            SELECT i.file
            FROM PostImage i JOIN i.file
            WHERE i.post.id = :postId AND i.file.id in :fileIds
            """)
    List<File> findByPostId(@Param("postId") Long postId,@Param("fileIds") List<Long>fileIds);
}
