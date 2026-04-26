package com.capstone.arfly.community.repository;

import com.capstone.arfly.community.domain.PostImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage,Long> {

    @Query("""
                SELECT f.fileKey
                FROM PostImage i
                JOIN i.file f
                WHERE i.post.id = :postId
                ORDER BY i.orderIndex ASC
            """)
   List<String> findFilePathsByPostId(@Param("postId")Long postId);
}
