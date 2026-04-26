package com.capstone.arfly.community.repository;

import com.capstone.arfly.community.domain.Comment;
import com.capstone.arfly.community.dto.CommentDetailResponseDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {


    @Query(
            """
                    SELECT new com.capstone.arfly.community.dto.CommentDetailResponseDto(
                    c.id, m.id,m.nickName,c.content,c.createdAt
                    )
                    FROM Comment c
                    JOIN c.member m
                    where c.post.id = :postId
                    ORDER BY c.createdAt ASC
                    """
    )
    List<CommentDetailResponseDto> findCommentsWithAuthorByPostId(@Param("postId")Long postId);
}
