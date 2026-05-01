package com.capstone.arfly.community.repository;

import com.capstone.arfly.community.domain.CommentMention;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMentionRepository extends JpaRepository<CommentMention,Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CommentMention c WHERE c.comment.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
