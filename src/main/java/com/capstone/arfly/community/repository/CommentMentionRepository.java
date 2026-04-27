package com.capstone.arfly.community.repository;

import com.capstone.arfly.community.domain.CommentMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMentionRepository extends JpaRepository<CommentMention,Long> {
}
